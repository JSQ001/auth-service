/* eslint-disable */
import React, { Component } from 'react';
import SearchArea from 'widget/search-area';
import config from 'config';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import moment from 'moment';
import Table from 'widget/table';
import { Popover, Button, message, Checkbox, Alert, Modal } from 'antd';
import SelectSplitLine from './selectSplitLine';
import service from './service';

const style = {
  position: 'fixed',
  bottom: 0,
  marginLeft: '-35px',
  width: '100%',
  height: '50px',
  boxShadow: '0px -5px 5px rgba(0, 0, 0, 0.067)',
  background: '#fff',
  lineHeight: '50px',
  padding: '0 60px',
};

class NewExpInputTax extends Component {
  constructor(props) {
    super(props);
    this.state = {
      searchForm: [
        {
          type: 'input',
          id: 'documentNumber',
          colSpan: 6,
          label: this.$t('acp.requisitionNumber'), // '单据编号'
        },
        {
          type: 'value_list',
          label: this.$t('common.expense.type'), // '费用类型',
          id: 'expenseTypeId',
          options: [],
          valueListCode: 'useType',
          colSpan: 6,
        },
        {
          type: 'list',
          listType: 'select_authorization_user',
          options: [],
          id: 'applicantId',
          label: this.$t('common.applicant'), // '申请人',
          labelKey: 'userName',
          valueKey: 'userId',
          single: true,
          colSpan: 6,
        },
        {
          type: 'items',
          id: 'amount',
          colSpan: 6,
          items: [
            {
              type: 'inputNumber',
              id: 'amountFrom',
              label:
                props.match.params.transferType === 'FOR_SALE'
                  ? this.$t('tax.as.sale.amount.from') // '可视同销售金额从'
                  : this.$t('tax.transfer.tax.from'), // '可转出税额从'
            },
            {
              type: 'inputNumber',
              id: 'amountTo',
              label:
                props.match.params.transferType === 'FOR_SALE'
                  ? this.$t('tax.as.sale.amount.to') // '可视同销售金额至'
                  : this.$t('tax.transfer.tax.to'), // '可转出税额至'
            },
          ],
        },
        {
          type: 'select',
          id: 'companyId',
          label: this.$t('acp.company'),
          options: [],
          getUrl: `${config.mdataUrl}/api/company/by/tenant`,
          method: 'get',
          labelKey: 'name',
          valueKey: 'id',
          colSpan: 6,
        },
        {
          type: 'select',
          id: 'departmentId',
          label: this.$t('common.department'),
          options: [],
          getUrl: `${config.mdataUrl}/api/DepartmentGroup/selectDept/enabled`,
          method: 'get',
          labelKey: 'name',
          valueKey: 'departmentId',
          colSpan: 6,
        },
        {
          type: 'items',
          id: 'date',
          items: [
            { type: 'date', id: 'transferDateFrom', label: this.$t('common.happened.date.from') }, // '发生日期从'
            { type: 'date', id: 'transferDateTo', label: this.$t('common.happened.date.to') }, // '发生日期至'
          ],
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'description',
          colSpan: 6,
          label: this.$t('common.remark'),
        },
      ],
      searchParams: {},
      expInputTaxId: props.match.params.id, // 上一页面产生的headerId
      type: props.match.params.transferType, // 上一页面选择的业务大类，是否视同销售和转出
      page: 0,
      size: 10,
      saveLoading: false,
      pagination: {
        showSizeChanger: true,
        showQuickJumper: true,
        showTotal: (total, range) =>
          this.$t('common.show.total', {
            range0: `${range[0]}`,
            range1: `${range[1]}`,
            total,
          }),
      },
      loading: false,
      dataSources: [],
      splitModalVisible: false, // 分摊模态框是否可见
      splitParams: {},
      checkedAll: false,
      indeterminate: false,
      selectedRows: [],
      amountTotal: 0, // 报账金额
      turnAmountTotal: 0, // 转出税额
    };
  }

  componentDidMount = () => {
    this.getDataSources(true);
  };

  // 生成columns
  createColumns = () => {
    const { checkedAll, indeterminate } = this.state;
    const { match } = this.props;
    return [
      {
        title: (
          <Checkbox onChange={this.selectAll} indeterminate={indeterminate} checked={checkedAll} />
        ),
        dataIndex: 'selectFlag',
        align: 'center',
        render: (flag, record, index) => {
          return (
            <Checkbox
              checked={record.selectFlag === 'Y'}
              indeterminate={record.selectFlag === 'P'}
              onChange={e => this.calculateAmount(e, record, index)}
            />
          );
        },
        width: 50,
      },
      {
        title: this.$t('acp.requisitionNumber'),
        dataIndex: 'documentNumber',
        align: 'center',
        render: (value, record) => {
          return (
            <Popover content={value}>
              <a onClick={() => this.showDocumentDetail(record)}>{value}</a>
            </Popover>
          );
        },
        width: 160,
      },
      {
        title: this.$t('common.applicant'),
        dataIndex: 'fullName',
        align: 'center',
        render: value => {
          return <Popover content={value}>{value}</Popover>;
        },
        width: 120,
      },
      {
        title: this.$t('common.expense.type'),
        dataIndex: 'expenseTypeName',
        align: 'center',
        render: value => {
          return <Popover content={value}>{value}</Popover>;
        },
        width: 120,
      },
      {
        title: this.$t('common.happened.date'),
        dataIndex: 'transferDate',
        align: 'center',
        render: value => {
          return (
            <Popover content={moment(value).format('YYYY-MM-DD')}>
              {moment(value).format('YYYY-MM-DD')}
            </Popover>
          );
        },
        width: 120,
      },
      {
        title: this.$t('tax.reimbursement.amount'), // '报账金额'
        dataIndex: 'reportAmount',
        align: 'center',
        render: (amount, record) => (
          <span>
            <Popover
              content={
                <span>
                  {`${record.currencyCode} `}
                  {this.filterMoney(amount, 2, true)}
                </span>
              }
            >
              {`${record.currencyCode} `}
              {this.filterMoney(amount, 2, true)}
            </Popover>
          </span>
        ),
        width: 120,
      },
      {
        title:
          match.params.transferType === 'FOR_SALE'
            ? this.$t('tax.as.sale.amount')
            : this.$t('tax.transfer.tax'), // '可视同销售金额' : '可转出税额',
        dataIndex: 'ableAmount',
        align: 'center',
        render: (amount, record) => (
          <span>
            <Popover
              content={
                <span>
                  {`${record.currencyCode} `}
                  {this.filterMoney(amount, 2, true)}
                </span>
              }
            >
              {`${record.currencyCode} `}
              {this.filterMoney(amount, 2, true)}
            </Popover>
          </span>
        ),
        width: 120,
      },
      {
        title:
          match.params.transferType === 'FOR_SALE'
            ? this.$t('tax.sale.amount')
            : this.$t('tax.turnOut.tax'), // '视同销售金额' : '转出税额',
        dataIndex: 'baseAmount',
        align: 'center',
        render: (amount, record) => (
          <span>
            <Popover
              content={
                <span>
                  {`${record.currencyCode} `}
                  {this.filterMoney(amount, 2, true)}
                </span>
              }
            >
              {`${record.currencyCode} `}
              {this.filterMoney(amount, 2, true)}
            </Popover>
          </span>
        ),
        width: 120,
      },
      { title: this.$t('common.remark'), dataIndex: 'description', align: 'center' },
      {
        title: this.$t('common.operation'),
        dataIndex: 'operation',
        align: 'center',
        render: (value, record) => {
          return match.params.transferType === 'FOR_SALE' ? (
            <a onClick={() => this.saleingPart(record)}>
              {this.$t('tax.part.sale.amount')}
              {/* 部分视同销售 */}
            </a>
          ) : (
            <a onClick={() => this.rollingPart(record)}>
              {this.$t('tax.part.turnOut.tax')}
              {/* 部分转出 */}
            </a>
          );
        },
        width: 120,
      },
    ];
  };

  // 获取数据源 flag:决定是否要loading状态
  getDataSources = flag => {
    const { page, size, searchParams, expInputTaxId, selectedRows, pagination } = this.state;
    let { checkedAll } = this.state;
    if (flag) this.setState({ loading: true });
    const params = {
      page,
      size,
      ...searchParams,
      headerId: expInputTaxId,
    };

    service
      .getExpenseLine(params)
      .then(res => {
        const dataSources = res.data;
        pagination.total = Number(res.headers['x-total-count']) || 0;
        dataSources.forEach(item => {
          const row = selectedRows.find(o => o.expReportLineId === item.expReportLineId);
          if (row) {
            // eslint-disable-next-line no-param-reassign
            item.selectFlag = row.selectFlag;
            if (item.expInputForReportDistDTOS && item.expInputForReportDistDTOS.length) {
              item.expInputForReportDistDTOS.forEach(line => {
                const model = row.expInputForReportDistDTOS.find(
                  o => o.expReportDistId === line.expReportDistId
                );
                if (model) {
                  // eslint-disable-next-line no-param-reassign
                  line.selectFlag = model.selectFlag;
                }
              });
            }
          } else {
            selectedRows.push({
              ...item,
              expReportLineId: item.expReportLineId,
              selectFlag: checkedAll ? 'Y' : item.selectFlag,
              ableAmount: item.ableAmount,
              baseAmount: checkedAll ? item.ableAmount : item.baseAmount,
              reportAmount: item.reportAmount,
              expInputForReportDistDTOS: item.expInputForReportDistDTOS.map(o => ({
                ...o,
                expReportDistId: o.expReportDistId,
                selectFlag: checkedAll ? 'Y' : o.selectFlag,
              })),
            });
            // eslint-disable-next-line no-param-reassign
            item.selectFlag = checkedAll ? 'Y' : item.selectFlag;
            // eslint-disable-next-line no-param-reassign
            item.baseAmount = checkedAll ? item.ableAmount : item.baseAmount;
          }
        });

        this.setState({
          dataSources,
          loading: false,
          checkedAll,
          pagination,
        });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  // 渲染alert，显示勾选数据的amount
  renderAlertMsg = () => {
    const { type, selectedRows } = this.state;
    const { match } = this.props;
    if (selectedRows instanceof Array) {
      let sumReportAmount = 0;
      let sumBaseAmount = 0;
      let num = 0;
      const title =
        type === 'FOR_SALE' ? `${this.$t('tax.sale.amount')}` : `${this.$t('tax.turnOut.tax')}`; // '视同销售金额' : '转出税额';
      selectedRows.forEach(item => {
        const temp = { ...item };
        temp.reportAmount = temp.reportAmount || 0;
        temp.baseAmount = temp.baseAmount || 0;
        if (temp.selectFlag === 'Y' || temp.selectFlag === 'P') {
          sumReportAmount += Number(temp.reportAmount);
          sumBaseAmount += Number(temp.baseAmount);
          num += 1;
        }
      });
      return (
        <span>
          {/* 已选择 */}
          {`${this.$t('org.has-select')}`}
          <span style={{ color: '#108EE9', margin: '0 5px' }}>{num}</span>
          <span>
            {`${this.$t('tax.reimbursement.amount')} ${match.params.currencyCode}  `}
            {this.filterMoney(sumReportAmount)}
            {`  ${title} : ${match.params.currencyCode}  `}
            {this.filterMoney(sumBaseAmount)}
          </span>
        </span>
      );
    }
  };

  // 搜索
  handleSubmitSearch = values => {
    const { searchParams, expInputTaxId } = this.state;
    this.setState(
      {
        searchParams: {
          ...searchParams,
          ...values,
          applicantId: values.applicantId && values.applicantId[0] ? values.applicantId[0] : '',
          headerId: expInputTaxId,
          transferDateFrom:
            values.transferDateFrom && moment(values.transferDateFrom).format('YYYY-MM-DD'),
          transferDateTo:
            values.transferDateTo && moment(values.transferDateTo).format('YYYY-MM-DD'),
        },
      },
      () => {
        this.getDataSources(true);
      }
    );
  };

  // 重置搜索
  handleClearSearch = () => {
    this.setState({ searchParams: {} }, () => {
      this.getDataSources(true);
    });
  };

  // 勾选所有或取消所有---现在的保存接口是勾选一行即时触发
  selectAll = e => {
    const { dataSources, selectedRows } = this.state;
    let { checkedAll, indeterminate } = this.state;

    dataSources.forEach(item => {
      const row = selectedRows.find(o => o.expReportLineId === item.expReportLineId);
      if (row) {
        row.baseAmount = e.target.checked ? item.ableAmount : 0;
        row.selectFlag = e.target.checked ? 'Y' : 'N';
      }
      if (item.expInputForReportDistDTOS && item.expInputForReportDistDTOS.length) {
        // eslint-disable-next-line no-param-reassign
        item.expInputForReportDistDTOS.forEach((value, i) => {
          // eslint-disable-next-line no-param-reassign
          value.selectFlag = e.target.checked ? 'Y' : 'N';
          row.expInputForReportDistDTOS[i].selectFlag = e.target.checked ? 'Y' : 'N';
        });
      }
      // eslint-disable-next-line no-param-reassign
      item.baseAmount = e.target.checked ? item.ableAmount : 0;
      // eslint-disable-next-line no-param-reassign
      item.selectFlag = e.target.checked ? 'Y' : 'N';
    });

    if (e.target.checked) {
      if (dataSources.some(o => o.selectFlag === 'N' || !o.selectFlag)) {
        checkedAll = false;
        indeterminate = true;
      } else {
        checkedAll = true;
        indeterminate = false;
      }
    } else {
      checkedAll = false;
      indeterminate = false;
    }

    this.setState({ dataSources, checkedAll, indeterminate, selectedRows });
  };

  // 勾选单行-计算数额-checkbox
  calculateAmount = (e, record, index) => {
    const { dataSources, selectedRows } = this.state;
    let { checkedAll, indeterminate } = this.state;
    const data = dataSources[index];
    const row = selectedRows.find(o => o.expReportLineId === data.expReportLineId);
    if (data.selectFlag === 'N' || data.selectFlag === 'P' || !data.selectFlag) {
      row.selectFlag = 'Y';
      data.selectFlag = 'Y';

      data.expInputForReportDistDTOS.forEach((value, i) => {
        // eslint-disable-next-line no-param-reassign
        value.selectFlag = 'Y';
        row.expInputForReportDistDTOS[i].selectFlag = 'Y';
      });
      data.baseAmount = data.ableAmount;
      row.baseAmount = data.ableAmount;
      if (dataSources.some(o => o.selectFlag === 'N' || !o.selectFlag)) {
        indeterminate = true;
        checkedAll = false;
      } else {
        indeterminate = false;
        checkedAll = true;
      }
    } else if (data.selectFlag === 'Y') {
      data.selectFlag = 'N';
      data.baseAmount = 0;
      row.selectFlag = 'N';
      row.baseAmount = 0;

      data.expInputForReportDistDTOS.forEach((value, i) => {
        // eslint-disable-next-line no-param-reassign
        value.selectFlag = 'N';
        row.expInputForReportDistDTOS[i].selectFlag = 'Y';
      });

      if (dataSources.some(o => o.selectFlag === 'Y')) {
        indeterminate = true;
        checkedAll = false;
      } else {
        indeterminate = false;
        checkedAll = false;
      }
    }

    this.setState({ dataSources, checkedAll, indeterminate, selectedRows });
  };

  // 保存行编辑
  saveSplitValue = (expReportLineId, params, flag) => {
    const { dataSources } = this.state;
    const record = dataSources.find(o => o.expReportLineId === expReportLineId);
    if (flag === 'N') {
      record.baseAmount = 0;
    } else if (flag === 'Y') {
      record.baseAmount = record.ableAmount;
    } else {
      record.baseAmount = params.reduce((prev, current) => {
        if (current.selectFlag === 'Y') {
          prev += current.baseAmount;
        }
        return prev;
      }, 0);
    }
    record.selectFlag = flag;
    record.expInputForReportDistDTOS = params;
    this.setState({ dataSources, splitModalVisible: false, splitParams: {} });
  };

  // 展示单据编号对应详情
  showDocumentDetail = () => {};

  // 部分转出模态框
  rollingPart = record => {
    this.setState({ splitModalVisible: true, splitParams: { ...record } });
  };

  // 部分视同销售模态框
  saleingPart = record => {
    this.setState({ splitModalVisible: true, splitParams: { ...record } });
  };

  // 关闭模态框
  onCloseModal = () => {
    this.setState({ splitModalVisible: false, splitParams: {} });
  };

  // 分页
  tablePageChange = pagination => {
    this.setState(
      {
        page: pagination.current - 1,
        size: pagination.pageSize,
      },
      () => {
        this.getDataSources(true);
      }
    );
  };

  // 下一步
  handleNextStep = () => {
    const { dispatch, match } = this.props;
    const { selectedRows } = this.state;
    this.setState({ saveLoading: true });
    const data = selectedRows.filter(o => o.expInputForReportDistDTOS.length);
    service
      .saveExpenseLine(data)
      .then(() => {
        this.setState({ saveLoading: false });
        dispatch(
          routerRedux.push({
            pathname: `/exp-input-tax/exp-input-tax/input-tax-business-receipt/${match.params.id}/${
              match.params.transferType
            }`,
          })
        );
      })
      .catch(err => {
        message.error(err.response.data.message);
        this.setState({ saveLoading: false });
      });
  };

  // 取消
  handleCancel = () => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: '/exp-input-tax/exp-input-tax/exp-input-tax',
      })
    );
  };

  render() {
    const {
      searchForm,
      dataSources,
      loading,
      splitModalVisible,
      type,
      splitParams,
      pagination,
      saveLoading,
    } = this.state;
    const columns = this.createColumns();

    return (
      <div>
        <div style={{ paddingBottom: 64 }}>
          <SearchArea
            searchForm={searchForm}
            submitHandle={this.handleSubmitSearch}
            clearHandle={this.handleClearSearch}
            maxLength={4}
          />
          <Alert message={this.renderAlertMsg(dataSources)} type="info" showIcon />
          <Table
            style={{ margin: '10px 0' }}
            columns={columns}
            rowKey={record => record.expReportLineId}
            size="middle"
            bordered
            dataSource={dataSources}
            loading={loading}
            pagination={pagination}
            onChange={this.tablePageChange}
            scroll={{ x: 700 }}
          />
        </div>
        <Modal
          visible={splitModalVisible}
          title={
            type === 'FOR_SALE'
              ? this.$t('tax.sale.share.row.select')
              : this.$t('tax.turnOut.share.row.select')
            // '选择需视同销售的分摊行' : '选择需转出的分摊行'
          }
          onCancel={this.onCloseModal}
          width={800}
          destroyOnClose
          footer={null}
        >
          <SelectSplitLine
            splitParams={splitParams}
            type={type}
            onCancel={this.onCloseModal}
            onSave={this.saveSplitValue}
          />
        </Modal>
        <div style={style}>
          <Button
            loading={saveLoading}
            type="primary"
            style={{ marginRight: '20px' }}
            onClick={this.handleNextStep}
          >
            {/* 下一步 */}
            {this.$t('acp.next')}
          </Button>
          <Button onClick={this.handleCancel}>{this.$t('common.cancel')}</Button>
        </div>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    company: state.user.company,
    user: state.user.currentUser,
  };
}
export default connect(mapStateToProps)(NewExpInputTax);

import React from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import config from 'config';
import SearchArea from 'widget/search-area';
import { Input, Button, message, Popover, Row, Col, Badge, Modal, Divider } from 'antd';
// import Table from 'widget/table';
import CustomTable from 'components/Widget/custom-table';
import ExcelExporter from 'widget/excel-exporter';
import ListSelector from 'widget/list-selector';
import moment from 'moment';
import PayDetail from 'containers/pay/pay-workbench/payment-detail';
import FileSaver from 'file-saver';
import paymentService from './payment-view.service';

const { Search } = Input;

class PaymentView extends React.Component {
  constructor(props) {
    super(props);
    this.pamentRequisitionStatus = {
      1001: { label: this.$t('acp.new' /* 编辑中 */), state: 'default' },
      1002: { label: this.$t('acp.approving' /* 审批中 */), state: 'processing' },
      1003: { label: this.$t('acp.returned' /* 已撤回 */), state: 'warning' },
      1004: { label: this.$t('acp.approved' /* 审批通过 */), state: 'success' },
      1005: { label: this.$t('acp.rejected' /* 审批驳回 */), state: 'error' },
    };
    this.state = {
      /**
       * 查询条件
       */
      searchForm: [
        {
          type: 'list',
          id: 'companyId',
          label: '单据公司',
          colSpan: 6,
          listType: 'available_company',
          valueKey: 'id',
          labelKey: 'name',
          listExtraParams: { setOfBooksId: props.company.setOfBooksId },
          single: true,
          event: 'company',
        },
        {
          type: 'select',
          id: 'acpReqTypeId',
          label: this.$t('acp.typeName' /* 单据类型 */),
          options: [],
          getUrl: `${config.payUrl}/api/acp/request/type/query/${props.company.setOfBooksId}/${
            props.company.id
          }`,
          method: 'get',
          labelKey: 'description',
          valueKey: 'id',
          colSpan: 6,
        },
        {
          type: 'list',
          id: 'employeeId',
          label: this.$t('acp.employeeName' /* 申请人 */),
          colSpan: '6',
          listExtraParams: { setOfBooksId: props.company.setOfBooksId },
          listType: 'bgtUser',
          valueKey: 'id',
          labelKey: 'fullName',
          single: true,
        },
        {
          type: 'select',
          id: 'status',
          label: this.$t('acp.status' /* 状态 */),
          colSpan: 6,
          options: [
            { value: '1001', label: this.$t('acp.new' /* 编辑中 */) },
            { value: '1002', label: this.$t('acp.approving' /* 审批中 */) },
            { value: '1003', label: this.$t('acp.returned' /* 已撤回 */) },
            { value: '1004', label: this.$t('acp.approved' /* 审批通过 */) },
            { value: '1005', label: this.$t('acp.rejected' /* 审批驳回 */) },
          ],
        },
        {
          type: 'list',
          id: 'unitId',
          label: '单据部门',
          colSpan: 6,
          listExtraParams: {},
          listType: 'department',
          labelKey: 'name',
          valueKey: 'departmentId',
          single: true,
        },
        {
          type: 'items',
          id: 'dateRange',
          items: [
            { type: 'date', id: 'requisitionDateFrom', label: '申请日期从' },
            { type: 'date', id: 'requisitionDateTo', label: '申请日期至' },
          ],
          colSpan: 6,
        },
        {
          type: 'items',
          id: 'payAmountRange',
          items: [
            {
              type: 'inputNumber',
              id: 'payAmountFrom',
              label: '已付金额从',
            },
            {
              type: 'inputNumber',
              id: 'payAmountTo',
              label: '已付金额至',
            },
          ],
          colSpan: 6,
        },
        {
          type: 'items',
          id: 'amountRange',
          items: [
            {
              type: 'inputNumber',
              id: 'functionAmountFrom',
              label: '申请金额从',
            },
            {
              type: 'inputNumber',
              id: 'functionAmountTo',
              label: '申请金额至',
            },
          ],
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'description',
          label: '备注',
          colSpan: 6,
        },
      ],
      columns: [
        {
          title: this.$t('acp.requisitionNumber' /* 单据编号 */),
          dataIndex: 'requisitionNumber',
          align: 'center',
          width: 180,
          render: (requisitionNumber, record) => {
            return (
              <Popover content={requisitionNumber}>
                <a onClick={() => this.handleLink(record)}>{requisitionNumber}</a>
              </Popover>
            );
          },
        },
        {
          title: this.$t('acp.typeName' /* 单据类型 */),
          dataIndex: 'acpReqTypeName',
          align: 'center',
          width: 160,
          render: acpReqTypeName => {
            return <Popover content={acpReqTypeName}>{acpReqTypeName}</Popover>;
          },
        },
        {
          title: '单据公司',
          dataIndex: 'companyName',
          align: 'center',
          width: 160,
          render: desc => <Popover content={desc}>{desc || '-'}</Popover>,
        },
        {
          title: '单据部门',
          dataIndex: 'unitName',
          align: 'center',
          width: 100,
          render: desc => <Popover content={desc}>{desc || '-'}</Popover>,
        },
        {
          title: this.$t('acp.employeeName' /* 申请人 */),
          dataIndex: 'employeeName',
          align: 'center',
          width: 80,
          render: desc => <Popover content={desc}>{desc || '-'}</Popover>,
        },
        {
          title: this.$t('acp.requisitionDate' /* 申请日期 */),
          dataIndex: 'requisitionDate',
          align: 'center',
          width: 120,
          render: requisitionDate => {
            return <span>{moment(requisitionDate).format('YYYY-MM-DD')}</span>;
          },
        },
        {
          title: '币种',
          dataIndex: 'currency',
          align: 'center',
          width: 100,
          render: currency => {
            return <Popover content={currency}>{currency}</Popover>;
          },
        },
        {
          title: '申请金额',
          dataIndex: 'functionAmount',
          align: 'center',
          width: 110,
          render: recode => (
            <span>
              <Popover content={this.filterMoney(recode, 2)}>{this.filterMoney(recode, 2)}</Popover>
            </span>
          ),
        },
        {
          title: '已付金额',
          dataIndex: 'payAmount',
          align: 'center',
          width: 110,
          render: recode => (
            <span>
              <Popover content={this.filterMoney(recode, 2)}>{this.filterMoney(recode, 2)}</Popover>
            </span>
          ),
        },
        {
          title: this.$t('acp.status' /* 状态 */),
          dataIndex: 'status',
          align: 'center',
          width: 110,
          render: value => (
            <Badge
              status={this.pamentRequisitionStatus[value].state}
              text={this.pamentRequisitionStatus[value].label}
            />
          ),
        },
        {
          title: this.$t('acp.remark' /* 备注 */),
          dataIndex: 'description',
          align: 'center',
          width: 120,
          render: description => {
            return <Popover content={description}>{description}</Popover>;
          },
        },
        {
          title: '查看信息',
          dataIndex: 'view',
          width: '100',
          render: (view, record, index) => {
            return (
              <div>
                <a onClick={e => this.reportClick(e, record, index)}>报账单</a>
                <Divider type="vertical" />
                <a onClick={e => this.paymentDetail(e, record, index)}>支付明细</a>
              </div>
            );
          },
        },
      ],
      searchParam: {},
      loading: false,
      pagination: {
        total: 0,
        showSizeChanger: true,
        showQuickJumper: true,
      },
      data: [],
      showChild: false,
      detailId: undefined,
      /**
       * 弹窗
       */
      lsVisible: false,
      extraParams: {},
      selectorItem: {},
      /**
       * 导出
       */
      excelVisible: false,
      btLoading: false,
      exportColumns: [
        { title: '单据编号', dataIndex: 'requisitionNumber' },
        { title: '单据类型', dataIndex: 'acpReqTypeName' },
        { title: '单据公司', dataIndex: 'companyName' },
        { title: '单据部门', dataIndex: 'unitName' },
        { title: '申请人', dataIndex: 'employeeName' },
        { title: '创建人', dataIndex: 'createdName' },
        { title: '申请日期', dataIndex: 'requisitionDate' },
        { title: '币种', dataIndex: 'currency' },
        { title: '汇率', dataIndex: 'exchangeRate' },
        { title: '金额', dataIndex: 'functionAmount' },
        { title: '已付金额', dataIndex: 'payAmount' },
        { title: '状态', dataIndex: 'status' },
        { title: '备注', dataIndex: 'description' },
      ],
    };
  }

  handleLink = record => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.replace({
        pathname: `/my-payment-requisition/payment-requisition-detail/${record.id}`,
      })
    );
  };

  onBudgetJournalSearch = params => {
    const { searchParam } = this.state;
    this.setState(
      {
        loading: true,
        page: 0,
        searchParam: { ...searchParam, requisitionNumber: params },
      },
      () => {
        this.table.search(this.state.searchParam);
      }
    );
  };

  // 详情页
  rowClick = record => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.replace({
        pathname: ``,
      })
    );
  };

  /**
   * 搜索
   */
  searh = params => {
    if (params.companyId && params.companyId[0]) {
      params.companyId = params.companyId[0];
    }
    if (params.unitId && params.unitId[0]) {
      params.unitId = params.unitId[0];
    }
    this.setState(
      {
        loading: true,
        page: 0,
        searchParam: params,
      },
      () => {
        const { searchParam } = this.state;
        this.table.search(searchParam);
      }
    );
  };

  /**
   * 清空
   */
  clear = () => {
    let { searchForm } = this.state;
    searchForm[4].listExtraParams = {};
    this.setState(
      {
        loading: true,
        page: 0,
        searchParam: {},
        searchForm,
      },
      () => {
        this.table.search({});
      }
    );
  };

  /**
   * 点击支付明细超链接
   */
  paymentDetail = (e, record, index) => {
    e.preventDefault();
    let { extraParams, selectorItem } = this.state;
    const functionAmount = this.filterMoney(record.functionAmount, 2, true);
    const operationTypeList = {
      reserved: '反冲',
      refund: '退票',
      payment: '付款',
      return: '退款',
    };
    selectorItem = {
      title: '支付明细',
      url: `${config.payUrl}/api/cash/transaction/details/getDeatils/by/documentId`,
      searchForm: [
        {
          type: 'input',
          id: 'requisitionNumber',
          label: '付款单单号',
          disabled: true,
          defaultValue: record.requisitionNumber,
          colSpan: '6',
        },
        {
          type: 'input',
          id: 'acpReqTypeName',
          label: '付款单类型',
          disabled: true,
          defaultValue: record.acpReqTypeName,
          colSpan: '6',
        },
        {
          type: 'input',
          id: 'functionAmount',
          label: '金额',
          disabled: true,
          defaultValue: functionAmount,
          colSpan: '6',
        },
        {
          type: 'input',
          id: 'billCode',
          label: '付款流水号',
          colSpan: '6',
        },
      ],
      columns: [
        {
          title: '付款流水号',
          dataIndex: 'billcode',
          render: (desc, records) => (
            <Popover content={desc}>
              <a onClick={() => this.viewPayDetail(records.id)}>{desc || '-'}</a>
            </Popover>
          ),
        },
        {
          title: '操作类型',
          dataIndex: 'operationType',
          render: operationType => {
            return <span>{operationTypeList[operationType]}</span>;
          },
        },
        {
          title: '付款方账户',
          dataIndex: 'draweeAccount',
          render: (draweeAccount, records) => {
            const content = (
              <div>
                <div>
                  <span>户名：</span>
                  <span>{records.draweeAccountName}</span>
                </div>
                <div>
                  <span>账号：</span>
                  <span>{records.draweeBankNumber}</span>
                </div>
              </div>
            );
            return <Popover content={content}>{content}</Popover>;
          },
        },
        {
          title: '付款日期',
          dataIndex: 'scheduleDate',
          render: scheduleDate => {
            return <span>{moment(scheduleDate).format('YYYY-MM-DD')}</span>;
          },
        },
        { title: '币种', dataIndex: 'currency' },
        {
          title: '付款金额',
          dataIndex: 'amount',
          render: amount => {
            return <span>{this.filterMoney(amount, 2)}</span>;
          },
        },
        {
          title: '收款方账户',
          dataIndex: 'payeeAccount',
          render: (payeeAccount, records) => {
            const content = (
              <div>
                <div>
                  <span>户名：</span>
                  <span>{records.payeeAccountName}</span>
                </div>
                <div>
                  <span>账号：</span>
                  <span>{records.payeeAccountNumber}</span>
                </div>
              </div>
            );
            return <Popover content={content}>{content}</Popover>;
          },
        },
        { title: '收款方', dataIndex: 'partnerCategoryName' },
        { title: '收款人', dataIndex: 'draweeName' },
      ],
      key: 'id',
    };
    extraParams = {
      headerId: record.id,
      documentCategory: 'PAYMENT_REQUISITION',
    };
    this.setState({
      lsVisible: true,
      extraParams,
      selectorItem,
    });
  };

  // 查看支付流水详情
  viewPayDetail = id => {
    this.setState({
      showChild: true,
      detailId: id,

      detailFlag: 'PAYDETAIL',
    });
  };

  // 弹出框关闭
  onClose = () => {
    this.setState({
      showChild: false,
    });
  };

  //公司与部门级联
  formChange = (event, value) => {
    let { searchForm } = this.state;
    searchForm[4].listExtraParams = { companyId: value[0].id };
    this.formRef.setValues({ unitId: '' });
    this.setState({ searchForm });
  };

  /**
   * 点击导出按钮
   */
  onExportClick = () => {
    this.setState({
      btLoading: true,
      excelVisible: true,
    });
  };

  /**
   * 导出取消
   */
  onExportCancel = () => {
    this.setState({
      btLoading: false,
      excelVisible: false,
    });
  };

  /**
   * 确定导出
   */
  export = result => {
    const hide = message.loading('正在生成文件，请等待......');

    const { exportParams } = this.state;
    paymentService
      .export(result, exportParams)
      .then(res => {
        if (res.status === 200) {
          message.success('操作成功');
          const fileName = res.headers['content-disposition'].split('filename=')[1];
          const f = new Blob([res.data]);
          FileSaver.saveAs(f, decodeURIComponent(fileName));
          this.setState({
            btLoading: false,
          });
          hide();
        }
      })
      .catch(e => {
        message.error('下载失败，请重试!');
        this.setState({
          btLoading: false,
        });
        hide();
      });
  };

  wrapClose = content => {
    const { detailId, showChild } = this.state;
    const newProps = {
      params: { id: detailId, refund: true, flag: showChild },
    };
    return React.createElement(content, Object.assign({}, newProps.params, newProps));
  };

  render() {
    const { searchForm, params, showChild } = this.state;
    // 返回列表
    const { columns, pagination, loading, data } = this.state;
    // 弹窗
    const { lsVisible, extraParams, selectorItem } = this.state;
    // 导出
    const { exportColumns, excelVisible, btLoading } = this.state;
    return (
      <div>
        <SearchArea
          searchForm={searchForm}
          submitHandle={this.searh}
          clearHandle={this.clear}
          eventHandle={this.formChange}
          maxLength={4}
          wrappedComponentRef={inst => (this.formRef = inst)}
        />
        <div className="divider" />
        <div className="table-header">
          <div className="table-header-buttons">
            <Row>
              <Col span={18}>
                <Button loading={btLoading} type="primary" onClick={this.onExportClick}>
                  导出付款申请单
                </Button>
              </Col>
              <Col span={6}>
                <Search
                  placeholder="请输入付款申请单单据编号"
                  onSearch={this.onBudgetJournalSearch}
                  enterButton
                />
              </Col>
            </Row>
          </div>
        </div>
        <CustomTable
          ref={ref => (this.table = ref)}
          scroll={{ x: 1850 }}
          columns={columns}
          tableKey="id"
          onClick={this.rowClick}
          url={`${config.payUrl}/api/acp/requisition/header/query/dto`}
        />
        {/* 弹出框 */}
        <ListSelector
          selectorItem={selectorItem}
          extraParams={extraParams}
          visible={lsVisible}
          onOk={() => this.setState({ lsVisible: false })}
          onCancel={() => this.setState({ lsVisible: false })}
          hideRowSelect
          hideFooter
          modalWidth="70%"
        />
        {/* 导出 */}
        <ExcelExporter
          visible={excelVisible}
          onOk={this.export}
          columns={exportColumns}
          canCheckVersion={false}
          fileName="付款申请单"
          onCancel={this.onExportCancel}
          excelItem="PAYMENT_REQUISITION"
        />
        <Modal
          visible={showChild} // 支付明细弹窗
          footer={[
            <Button key="back" size="large" onClick={this.onClose}>
              {this.$t({ id: 'pay.backlash.goback' })}
            </Button>,
          ]}
          width={1200}
          closable={false}
          onCancel={this.onClose}
        >
          <div>{this.wrapClose(PayDetail)}</div>
        </Modal>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
    organization: state.user.organization || {},
  };
}

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(PaymentView);

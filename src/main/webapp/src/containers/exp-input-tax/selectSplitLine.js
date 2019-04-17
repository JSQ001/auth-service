import React, { Component } from 'react';
import { Alert, Popover, message, Button, Modal } from 'antd';
import Table from 'widget/table';
import service from './service';

class SelectSplitLine extends Component {
  constructor(props) {
    super(props);
    this.state = {
      columns: [
        {
          title: this.$t('expense-report.association.request'), // '关联申请单'
          dataIndex: 'applicationNumber',
          align: 'center',
          render: (value, record) => {
            return (
              <Popover content={value}>
                <a onClick={e => this.showDocumentDetail(e, record)}>{value}</a>
              </Popover>
            );
          },
          width: 120,
        },
        {
          title: this.$t('acp.company'), // '公司'
          dataIndex: 'companyName',
          align: 'center',
          render: value => {
            return (
              <Popover content={value}>
                <span>{value}</span>
              </Popover>
            );
          },
          width: 120,
        },
        {
          title: this.$t('common.department'),
          dataIndex: 'departmentName',
          align: 'center',
          render: value => {
            return (
              <Popover content={value}>
                <span>{value}</span>
              </Popover>
            );
          },
          width: 120,
        },
        {
          title: this.$t('expense.apportion.amount'), // '分摊金额',
          dataIndex: 'amount',
          align: 'center',
          render: amount => {
            return <Popover content={amount}>{this.filterMoney(amount, 2)}</Popover>;
          },
          width: 120,
        },
        {
          title: this.$t('tax.share.tax'), // '分摊税额',
          dataIndex: 'shareTax',
          align: 'center',
          render: amount => {
            return <Popover content={amount}>{this.filterMoney(amount, 2)}</Popover>;
          },
          width: 120,
        },
      ],
      dataSources: [],
      selectedRowKeys: [], // 存放选中的分摊行 `id-分摊税额`,
      // selectedRows: [], // 存放选中的分摊行数据
      sum: props.splitParams.baseAmount,
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
    };
  }

  // first--调用接口获取维度数据，动态渲染columns，second-调用接口获取行数据
  componentDidMount = () => {
    const {
      splitParams: { expInputForReportDistDTOS },
    } = this.props;
    const selectedRowKeys = expInputForReportDistDTOS
      .filter(o => o.selectFlag === 'N')
      .map(o => o.expReportDistId);
    this.setState({ selectedRowKeys });
  };

  // 关联报账单
  showDocumentDetail = e => {
    e.preventDefault();
  };

  // 获取行数据
  getTableList = () => {
    this.setState({
      dataSources: [
        {
          id: '10',
          applicationNumber: '1000323',
          companyName: 'company',
          departmentName: 'dp1',
          amount: 1000,
          shareTax: 800,
          costCenter: [{ costCenter1Id: { name: 'cc1', id: '1001' } }],
        },
        {
          id: '11',
          applicationNumber: '1111323',
          companyName: 'company',
          departmentName: 'dp2',
          amount: 2000,
          shareTax: 1800,
          costCenter: [{ costCenter1Id: { name: 'cc2', id: '1002' } }],
        },
      ],
    });
    if (true) return;
    const { splitParams } = this.props;
    service
      .getSplitLineList(splitParams.id)
      .then(res => {
        console.log(res);
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  // 动态增加成本中心字段至columns
  addCostCenterColumns = dimensionData => {
    const { columns } = this.state;
    if (dimensionData instanceof Array) {
      if (columns.length <= 5) {
        dimensionData.forEach(item => {
          // 维度id
          // const dimensionId = item.id;
          // 维度name
          const dimensionTitle = item.name;
          // 维值id-字段名称
          const dimensionKey = `dimensionValue${item.dimensionSequence}Id`;
          // 维值name-字段名称
          const dimensionName = `dimensionValue${item.dimensionSequence}Name`;
          const costCenterColumn = {
            title: dimensionTitle,
            dataIndex: dimensionKey,
            align: 'center',
            width: 120,
            render: (text, record) => {
              return <Popover content={text}>{record[dimensionName]}</Popover>;
            },
          };
          columns.splice(2, 0, costCenterColumn);
          this.setState({
            columns,
          });
        });
      }
    }
  };

  // 渲染 alert msg
  renderAlertMsg = data => {
    let sum = 0;
    if (data instanceof Array && data.length > 0) {
      sum = data.reduce((pre, cur) => {
        return pre + Number(cur.shareTax);
      }, 0);
    }
    this.setState({ sum });
  };

  // 初始渲染左侧checkbox 勾选情况
  initCheckBoxSelected = () => {
    const { splitParams } = this.props;
    const { selectedRowKeys } = this.state;
    if (
      splitParams.expInputForReportDistDTOS &&
      splitParams.expInputForReportDistDTOS instanceof Array
    ) {
      splitParams.expInputForReportDistDTOS.forEach(item => {
        if (item.selectFlag === 'Y') {
          selectedRowKeys.push(`${item.expReportDistId}-${item.baseAmount}`);
        }
      });
      this.setState({ selectedRowKeys });
    }
  };

  // 选择，获取分摊税额存入selectedRowKeys数组中
  handleGetShareTax = (selectedRowKeys, selectedRows) => {
    this.setState({ selectedRowKeys }, () => {
      this.renderAlertMsg(selectedRows);
    });
  };

  // 点击确定，计算分摊税额总值，并调接口存入后台，关闭模态框，并刷新页面
  handleOk = () => {
    const { selectedRowKeys, dataSources } = this.state;
    const { splitParams, onSave } = this.props;
    if (selectedRowKeys.length > 0) {
      const sumShareTax = selectedRowKeys.reduce((pre, cur) => {
        return pre + Number(cur.split('-')[1]);
      }, 0);
      const params = { ...splitParams };
      params.baseAmount = sumShareTax;
      /**
       * 1. 如果selectedRowKeys.length === dataSources.length
       *    表示全选，则splitParams的两个selectFlag = 'Y'
       * 2. 如果selectedRowKeys.length = 0
       *    表示不选，则splitParams的两个selectFlag = 'N'
       * 3. 非上者，循环splitParams.expInputForReportDistDTOS,
       *    将内部每个对象的selectFlag按照selectedRowKeys所拥有
       *    的进行对应修改
       */
      if (selectedRowKeys.length === dataSources.length) {
        params.selectFlag = 'Y';
        params.expInputForReportDistDTOS = params.expInputForReportDistDTOS.map(item => {
          const tempItem = { ...item };
          tempItem.selectFlag = 'Y';
          return tempItem;
        });
      } else if (selectedRowKeys.length === 0) {
        params.selectFlag = 'N';
        params.expInputForReportDistDTOS = params.expInputForReportDistDTOS.map(item => {
          const tempItem = { ...item };
          tempItem.selectFlag = 'N';
          return tempItem;
        });
      } else {
        debugger;
        params.selectFlag = 'P';
        const temp = [];
        selectedRowKeys.forEach(item => {
          params.expInputForReportDistDTOS.forEach(value => {
            // 分摊行的id === 从后台获取到的表格内分摊行id
            if (value.expReportDistId === item.split('-')[0]) {
              const tempValue = { ...value };
              tempValue.selectFlag = 'Y';
              temp.push(tempValue);
            } else temp.push(value);
          });
        });
        params.expInputForReportDistDTOS = temp;
      }
      console.log(params);
      if (params) return;
      onSave([params], true);
    } else {
      Modal.info({
        title: this.$t('common.info'),
        content: this.$t('tax.share.row.no.selected'), // '还未选择任何分摊行！',
      });
    }
  };

  // 分页
  tablePageChange = pagination => {
    const { page, size } = this.state;
    this.setState({
      page: pagination.current - 1 || page,
      size: pagination.pageSize || size,
    });
  };

  render() {
    const { columns, dataSources, selectedRowKeys, sum, pagination } = this.state;
    const { type } = this.props;
    const rowSelection = {
      selectedRowKeys,
      onChange: this.handleGetShareTax,
    };
    const { onCancel, splitParams } = this.props;
    return (
      <div>
        <Alert
          type="info"
          message={
            <span>
              {/* 已选择 */}
              {`${this.$t('org.has-select')}`}
              <span style={{ color: '#108EE9', margin: '0 5px' }}>{selectedRowKeys.length}</span>
              <span>
                {/* '视同销售金额: ' : '转出税额: ' */}
                {type === 'FOR_SALE' ? this.$t('tax.sale.amount') : this.$t('tax.turnOut.tax')}
                {` CNY `}
                {this.filterMoney(sum)}
              </span>
            </span>
          }
          showIcon
        />
        <Table
          columns={columns}
          dataSource={splitParams.expInputForReportDistDTOS}
          style={{ margin: '10px 0 30px 0' }}
          scroll={{ x: 700 }}
          rowSelection={rowSelection}
          rowKey={record => record.expReportDistId}
          pagination={pagination}
          onChange={this.tablePageChange}
        />
        <div className="slide-footer">
          <Button type="primary" onClick={this.handleOk}>
            {/* 确 定 */}
            {this.$t('common.ok')}
          </Button>
          <Button onClick={onCancel} style={{ marginRight: '40px' }}>
            {/* 返 回 */}
            {this.$t('budgetJournal.return')}
          </Button>
        </div>
      </div>
    );
  }
}

export default SelectSplitLine;

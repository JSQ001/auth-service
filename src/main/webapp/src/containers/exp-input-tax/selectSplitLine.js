import React, { Component } from 'react';
import { Alert, Popover, Button } from 'antd';
import Table from 'widget/table';

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
          width: 150,
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
          dataIndex: 'distAmount',
          align: 'center',
          render: amount => {
            return <Popover content={amount}>{this.filterMoney(amount, 2)}</Popover>;
          },
          width: 120,
        },
        {
          title: this.$t('tax.share.tax'), // '分摊税额',
          dataIndex: 'distTaxAmount',
          align: 'center',
          render: amount => {
            return <Popover content={amount}>{this.filterMoney(amount, 2)}</Popover>;
          },
          width: 120,
        },
      ],
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
      .filter(o => o.selectFlag === 'Y')
      .map(o => o.expReportDistId);
    this.setState({ selectedRowKeys });
  };

  // 关联报账单
  showDocumentDetail = e => {
    e.preventDefault();
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
    const { selectedRowKeys } = this.state;
    const {
      splitParams: { expInputForReportDistDTOS, expReportLineId },
      onSave,
    } = this.props;
    selectedRowKeys.forEach(value => {
      const record = expInputForReportDistDTOS.find(o => o.expReportDistId === value);
      record.selectFlag = 'Y';
    });

    let flag = 'N';
    if (expInputForReportDistDTOS.length) {
      if (expInputForReportDistDTOS.length === selectedRowKeys.length) {
        flag = 'Y';
      } else if (selectedRowKeys.length) {
        flag = 'P';
      } else {
        flag = 'N';
      }
    }

    onSave(expReportLineId, expInputForReportDistDTOS, flag);
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
    const { columns, selectedRowKeys, sum, pagination } = this.state;
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

import React, { Component } from 'react';
import { Modal, Alert, Input, Popover, message } from 'antd';
import Table from 'widget/table';
import reimburseService from 'containers/reimburse/my-reimburse/reimburse.service';
import { myConcat } from 'utils/utils';

class Verification extends Component {
  constructor(props) {
    super(props);
    this.state = {
      visible: false,
      columns: [
        {
          title: '关联申请单',
          dataIndex: 'sourceDocumentCode',
          key: 'sourceDocumentCode',
          width: 180,
          align: 'center',
          render: desc => <Popover content={desc}>{desc ? desc : '-'}</Popover>,
        },
        {
          title: '公司',
          dataIndex: 'companyName',
          key: 'companyName',
          align: 'center',
          width: 120,
          render: (value, record) => {
            return <span>{value}</span>;
          },
        },
        {
          title: '部门',
          dataIndex: 'departmentName',
          align: 'center',
          key: 'departmentName',
          width: 120,
          render: (value, record) => {
            return <span>{value}</span>;
          },
        },
        {
          title: '分摊金额',
          align: 'center',
          dataIndex: 'amount',
          key: 'amount',
          width: 100,
          render: desc => this.formatMoney(this.props.mode === 'negative' ? -desc : desc),
        },
      ],
      data: [],
      model: {},
      messageType: 'warning',
      pagination: {
        total: 0,
      },
      page: 0,
      pageSize: 5,
      loading: false,
      changeList: [],
      headerData: {},
      id: '',
      x: 0,
    };
  }

  componentWillReceiveProps(nextProps) {
    if (!nextProps.visible && this.props.visible) {
      this.setState({
        data: [],
        changeList: [],
        page: 0,
        pagination: {
          total: 0,
          current: this.state.page + 1,
        },
      });
    }

    //显示
    if (nextProps.visible && !this.props.visible) {
      this.setState(
        {
          visible: nextProps.visible,
          id: nextProps.id,
          loading: true,
          headerData: nextProps.headerData,
        },
        () => {
          this.getDimDetail(nextProps.params);
          this.getList();
        }
      );
    }
  }

  //维度配置
  getDimDetail = params => {
    let { columns } = this.state;
    reimburseService.getConfigDetail(params).then(res => {
      let arr = res.data.expenseDimensions || [];
      arr.reverse().map(item => {
        columns.splice(2, 0, {
          title: item.name,
          type: 'select',
          labelKey: 'dimensionItemName',
          valueKey: 'id',
          dataIndex: item.dimensionField,
          render: (desc, record) => record[item.dimensionField.replace('Id', 'Name')],
          width: 120,
          getOptions: () => this.getDimValue(item.dimensionId, item.dimensionField),
        });
      });
      this.setState({ columns: myConcat(columns, 'dataIndex') });
    });
  };

  //获取列表
  getList = () => {
    let model = { ...this.state.model, page: this.state.page };

    this.setState({ loading: true });
    const { page, size } = this.state;
    let params = {
      lineId: this.props.id,
      page: page,
      size: size,
    };
    reimburseService.getShareDetail(params).then(res => {
      this.setState({
        data: res.data,
        pagination: {
          total: res.data.length,
          current: this.state.page + 1,
          pageSize: this.state.pageSize,
          onChange: this.onChangePaper,
          showTotal: total => `共 ${total} 条数据`,
        },
        loading: false,
      });
    });
  };
  //分页
  onChangePaper = page => {
    if (page - 1 !== this.state.page) {
      this.setState({ page: page - 1 }, () => {
        this.getList();
      });
    }
  };

  handleCancel = () => {
    this.props.close && this.props.close();
  };

  change = (e, index) => {
    let data = this.state.data;
    let writeOffAmount = data[index].writeOffAmount;
    let model = this.state.model;
    data[index].writeOffAmount = e.target.value;

    let changeList = this.state.changeList;
    let record = changeList.find(
      o => data[index].cshTransactionDetailId == o.cshTransactionDetailId
    );
    if (record) {
      record.writeOffAmount = e.target.value;
    } else {
      changeList.push(data[index]);
    }

    if (writeOffAmount && e.target.value) {
      model.writeOffAmount += parseFloat(e.target.value) - parseFloat(writeOffAmount);
    } else if (!writeOffAmount && e.target.value) {
      model.writeOffAmount += parseFloat(e.target.value);
    } else if (!e.target.value && writeOffAmount) {
      model.writeOffAmount -= parseFloat(writeOffAmount);
    }

    this.setState({ data, changeList, model }, this.checkAmount);
  };

  //检查头上金额
  checkAmount = () => {
    let model = this.state.model;

    if (model.writeOffAmount > model.amount) {
      this.setState({ messageType: 'error' });
    } else {
      this.setState({ messageType: 'warning' });
    }
  };

  render() {
    const { data, columns, model, pagination, loading } = this.state;
    return (
      <Modal
        className="select-cost-type"
        title="分摊信息"
        visible={this.props.visible}
        onOk={this.handleOk}
        onCancel={this.handleCancel}
        width="65%"
        footer={false}
        rowkey={record => record.id}
      >
        <Table
          rowKey="id"
          loading={loading}
          pagination={pagination}
          bordered
          dataSource={data}
          columns={columns}
        />
      </Modal>
    );
  }
}

export default Verification;

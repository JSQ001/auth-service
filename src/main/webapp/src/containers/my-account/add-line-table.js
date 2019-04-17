import React, { Component } from 'react';
import {
  Form,
  Input,
  Button,
  Tooltip,
  Icon,
  Col,
  DatePicker,
  Select,
  InputNumber,
  Popconfirm,
  Divider,
  Popover,
  Spin,
  message,
} from 'antd';
import Table from 'widget/table';
import { connect } from 'dva';
import invoiceImg from 'images/expense/invoice-info.png';
import invoiceImgEn from 'images/expense/invoice-info-en.png';
const FormItem = Form.Item;
const Option = Select.Option;

class AddLineTable extends Component {
  constructor(props) {
    super(props);
    this.state = {
      columns: [
        {
          title: '序号',
          dataIndex: 'invoiceLineNum',
          render: (value, record, index) => {
            record.invoiceLineNum = index + 1;
            return index + 1;
          },
          align: 'center',
          width: '45px',
        },
        {
          title: '货物或应税劳务、服务名称',
          dataIndex: 'goodsName',
          align: 'center',
          render: (value, record, index) => {
            return this.tableRender(value, record, index, 'goodsName', 'input');
          },
          width: 160,
        },
        {
          title: '规格型号',
          dataIndex: 'specificationModel',
          width: 100,
          align: 'center',
          render: (value, record, index) => {
            return this.tableRender(value, record, index, 'specificationModel', 'input');
          },
        },
        {
          title: '单位',
          dataIndex: 'unit',
          width: 100,
          align: 'center',
          render: (value, record, index) => {
            return this.tableRender(value, record, index, 'unit', 'input');
          },
        },
        {
          title: '数量',
          dataIndex: 'num',
          width: 100,
          align: 'center',
          render: (value, record, index) => {
            return this.tableRender(value, record, index, 'num', 'number');
          },
        },
        {
          title: '单价',
          dataIndex: 'unitPrice',
          align: 'center',
          width: 100,
          render: (value, record, index) => {
            return this.tableRender(value, record, index, 'unitPrice', 'number');
          },
        },
        {
          title: '金额',
          dataIndex: 'detailAmount',
          width: 100,
          align: 'center',
          render: (value, record, index) => {
            return this.tableRender(value, record, index, 'detailAmount', 'number');
          },
        },
        {
          title: '税率',
          dataIndex: 'taxRate',
          align: 'center',
          width: 100,
          render: (value, record, index) => {
            return this.tableRender(value, record, index, 'taxRate', 'select');
          },
        },
        {
          title: '税额',
          dataIndex: 'taxAmount',
          align: 'center',
          width: 100,
          render: (value, record, index) => {
            return this.tableRender(value, record, index, 'taxAmount', 'number');
          },
        },
        {
          title: this.$t('common.operation'),
          dataIndex: 'operation',
          align: 'center',
          render: (value, record, index) => {
            if (record.isEdit) {
              return (
                <>
                  <a onClick={() => this.rowSave(index, record)}>保存</a>
                  <Divider type="vertical" />
                  <a onClick={() => this.rowEdit(index, false, record)}>取消</a>
                </>
              );
            } else {
              return (
                <>
                  <a onClick={() => this.rowEdit(index, true, record)}>编辑</a>
                  <Divider type="vertical" />
                  <Popconfirm title="确定删除?" onConfirm={() => this.rowDelete(index)}>
                    <a>删除</a>
                  </Popconfirm>
                </>
              );
            }
          },
          width: 100,
        },
      ],
      saveLoading: false,
      totalAmount: undefined,
      taxTotalAmount: undefined,
      dataSource: [],
      dataCache: [],
      pagination: {
        pageSize: 10,
      },
      spinLoading: true,
      currencyList: [],
      invoiceHeadList: [],
      purchaserList: [],
      sellerList: [],
      rowColumns: [],
      isShowInvoiceAmount: false,
      isShowTaxAmount: false,
      isShowNum: false,
      validateStatus: undefined,
      helpMessage: undefined,
      taxRate: ['0%', '1.5%', '3%', '5%', '10%', '11%', '13%', '16%', '17%'],
    };
  }

  componentDidMount() {}
  // 获取 index
  getFinalIndex = index => {
    const { current, pageSize } = this.state.pagination;
    let currentPage = current || 1;
    return (currentPage - 1) * pageSize + index;
  };

  handleChange = (key, value, record, index) => {
    console.log(value);
    const { dataSource, dataCache, isShowNum } = this.state;
    dataSource[index][key] = value;
    this.setState(
      {
        dataSource,
      },
      () => {}
    );
  };

  // 表格渲染
  tableRender = (desc, record, index, dataIndex, type) => {
    if (record.isEdit) {
      switch (type) {
        case 'input':
          return (
            <Input
              onChange={e => this.handleChange(dataIndex, e.target.value, record, index)}
              placeholder={this.$t('common.please.enter')}
              defaultValue={desc}
              size="small"
              title={desc}
            />
          );
        case 'number':
          return (
            <InputNumber
              step={0.01}
              onChange={value => this.handleChange(dataIndex, value, record, index)}
              precision={2}
              placeholder={this.$t('common.please.enter')}
              style={{ width: '100%' }}
            />
          );
        case 'select':
          return (
            <Select
              size="small"
              onChange={value => this.handleChange(dataIndex, value, record, index)}
              placeholder={this.$t('common.please.select')}
              style={{ width: '100%' }}
              dropdownMatchSelectWidth={false}
              getPopupContainer={trigger => trigger.parentNode}
            >
              {this.state.taxRate.map(item => <Option key={item}>{item}</Option>)}
            </Select>
          );
      }
    } else {
      return <Popover content={desc}>{desc}</Popover>;
    }
  };

  //加一行
  addRowTable = () => {
    const { dataSource, dataCache, isShowNum } = this.state;
    const { tenantId, setOfBooksId } = this.props.company;
    let data = {
      id: new Date().getTime(),
      isEdit: 'new',
      tenantId,
      setOfBooksId,
    };
    dataCache[dataSource.length] = data;
    dataSource.push(data);
    this.setState({ dataSource });
  };

  // 行编辑 flag: true 编辑，flag: false 取消
  rowEdit = (index, flag, record) => {
    const { dataSource, dataCache } = this.state;
    const finalIndex = this.getFinalIndex(index);
    if (flag) {
      dataCache[finalIndex] = { ...record };
      dataSource[finalIndex].isEdit = true;
    } else {
      if (record.isEdit === 'new') {
        dataSource.splice(finalIndex, 1);
        dataCache.splice(finalIndex, 1);
      } else {
        dataSource[finalIndex] = dataCache[finalIndex];
      }
    }
    this.setState({ dataSource });
  };

  // 行保存
  rowSave = (index, record) => {
    const { dataSource } = this.state;
    console.log(dataSource);
    const finalIndex = this.getFinalIndex(index);

    dataSource[finalIndex].isEdit = false;
    dataSource[finalIndex].closeAuto = false;
    this.setState({ dataSource });
  };

  // 行删除
  rowDelete = index => {
    const { dataSource, dataCache } = this.state;
    const finalIndex = this.getFinalIndex(index);
    dataSource.splice(finalIndex, 1);
    dataCache.splice(finalIndex, 1);
    this.setState({ dataSource });
    message.success('删除成功');
  };

  render() {
    const {
      invoiceHeadList,
      purchaserList,
      sellerList,
      saveLoading,
      columns,
      rowColumns,
      dataSource,
      pagination,
      spinLoading,
      validateStatus,
      helpMessage,
    } = this.state;

    return (
      <Spin size="large" spinning={false} wrapperClassName="new-invoice">
        <div style={{ margin: '15px 0' }}>
          <Button onClick={this.addRowTable}>
            <Icon type="plus" />
            {this.$t('expense.new.invoice.line')}
          </Button>
        </div>
        <Table
          rowKey="id"
          columns={columns}
          loading={false}
          dataSource={dataSource}
          pagination={pagination}
          size="small"
          scroll={{ x: 1300 }}
        />
      </Spin>
    );
  }
}

function mapStateToProps(state) {
  return {
    language: state.languages,
    company: state.user.company,
  };
}

export default connect(mapStateToProps)(AddLineTable);

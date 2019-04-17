import React, { Component } from 'react';
import {
  Input,
  Button,
  Icon,
  Select,
  InputNumber,
  Popconfirm,
  Divider,
  Popover,
  Spin,
  message,
  Form,
} from 'antd';
import Table from 'widget/table';
import { connect } from 'dva';

import PropTypes from 'prop-types';
import Chooser from '../../chooser';

class EditTable extends Component {
  constructor(props) {
    super(props);
    this.state = {
      columns: [],
      saveLoading: false,
      totalAmount: undefined,
      taxTotalAmount: undefined,
      dataSource: props.dataSource || [],
      dataCache: [],
      pagination: {
        page: 0,
        size: 10,
        onChange: this.handlePage,
      },
      spinLoading: true,
      currencyList: [],
      invoiceHeadList: [],
      purchaserList: [],
      sellerList: [],
      rowColumns: [],
      options: {},
      isShowInvoiceAmount: false,
      isShowTaxAmount: false,
      isShowNum: false,
      validateStatus: undefined,
      helpMessage: undefined,
      scroll: false,
      refresh: props.refresh,
      taxRate: ['0%', '1.5%', '3%', '5%', '10%', '11%', '13%', '16%', '17%'],
    };
  }

  componentDidMount() {
    this.getRender(this.props.columns);
  }

  componentWillReceiveProps = nextProps => {
    let isRender =
      nextProps.refresh !== this.state.refresh ||
      nextProps.columns.length !== this.state.columns.length ||
      nextProps.columns.length !== this.props.columns.length ||
      nextProps.dataSource.length !== this.props.dataSource.length;
    if (isRender) {
      this.getRender(nextProps.columns);
      this.setState({
        dataSource: nextProps.dataSource || this.state.dataSource,
      });
    }
  };

  getRender = columns => {
    let scroll = 0;
    columns = columns.map(item => {
      scroll += 120;

      let param = {
        ...item,
        width: 120,
        render: (value, record, index) => this.renderCol(value, record, index, item),
      };
      item.type === 'operation' && (item.fixed = 'right');
      item.type === 'sequence' &&
        (param.render = (value, record, index) => this.getFinalIndex(index + 1));
      return param;
    });
    this.setState({
      columns,
      scroll: scroll > 600 ? scroll : false,
    });
  };

  handlePage = (page, size) => {
    let pagination = this.state.pagination;
    pagination.page = page;
    this.setState({ pagination });
  };

  getFinalIndex = index => {
    const { page, size } = this.state.pagination;
    return index + page * size;
  };

  //给列添加render方法
  renderCol = (value, record, index, col) => {
    const { options } = this.state;
    if (record.isEdit) {
      switch (col.type) {
        case 'text': {
          return <span>{value}</span>;
        }
        case 'chooser': {
          return (
            <Chooser
              onChange={value => this.handleChange(col.dataIndex, value, index)}
              value={col.disabled ? col.defaultValue : value}
              title={value}
              disabled={col.disabled}
              type={col.listType}
              labelKey={col.labelKey}
              valueKey={col.valueKey}
              listExtraParams={col.listExtraParams}
              single={true}
              placeholder={this.$t('common.please.enter')}
            />
          );
        }
        case 'input': {
          return (
            <Input
              onChange={e => this.handleChange(col.dataIndex, e.target.value, index)}
              defaultValue={value}
              size="small"
              title={value}
              placeholder={this.$t('common.please.enter')}
            />
          );
        }
        case 'inputNumber': {
          // col.dataIndex ==='detailAmount'&&console.log(value)
          return (
            <InputNumber
              step={0.01}
              onChange={e => this.handleChange(col.dataIndex, Number(e), index)}
              precision={2}
              defaultValue={value}
              value={value} ///修改发票联动计算时添加
              style={{ width: '100%' }}
              placeholder={this.$t('common.please.enter')}
              {...col.override}
            />
          );
        }
        case 'select': {
          console.log(value);
          return (
            <Select
              onChange={value => this.handleChange(col.dataIndex, value, index)}
              defaultValue={value}
              value={value}
              onFocus={col.getOptions ? col.getOptions : () => {}}
              size="small"
              style={{ width: '100%' }}
              title={value}
              placeholder={this.$t('common.please.select')}
            >
              {(col.options || []).map(item => (
                <Select.Option key={item[col.valueKey || 'key']}>
                  {item[col.labelKey || 'label']}
                </Select.Option>
              ))}
            </Select>
          );
        }
        case 'operation': {
          return (
            <span>
              <a onClick={() => this.save(index, record)}>保存</a>
              <Divider type="vertical" />
              <a onClick={() => this.edit(index, false, record)}>取消</a>
            </span>
          );
        }
      }
    } else {
      if (col.type === 'operation') {
        return (
          <span>
            <a onClick={() => this.edit(index, true, record)}>编辑</a>
            <Divider type="vertical" />
            <Popconfirm title="确定删除?" onConfirm={() => this.delete(index)}>
              <a>删除</a>
            </Popconfirm>
          </span>
        );
      } else {
        value &&
          value.length &&
          col.type === 'chooser' &&
          (value = value[0][col.showName || 'name']);
        if (value && col.type === 'select' && col.options) {
          value = col.options.find(
            item => item[col.valueKey || 'key'].toString() === value.toString()
          )[col.labelKey || 'label'];
        }
        return <Popover content={value}>{value}</Popover>;
      }
    }
  };

  handleChange = (key, value, index) => {
    const { dataSource, dataCache, isShowNum } = this.state;
    const { handleEvent } = this.props;
    dataSource[this.getFinalIndex(index)][key] = value;
    this.setState(
      {
        dataSource,
      },
      () => {
        handleEvent && handleEvent(key, value, this.getFinalIndex(index));
      }
    );
  };

  //加一行
  addRowTable = () => {
    const { dataSource, dataCache, pagination } = this.state;
    const { tenantId, setOfBooksId } = this.props.company;
    const { checkMethod } = this.props; //校验方法
    if (checkMethod) {
      if (!checkMethod()) {
        return;
      }
    }
    let data = {
      key: this.getFinalIndex(dataSource.length),
      isEdit: true,
      tenantId,
      setOfBooksId,
    };
    //dataCache[dataSource.length] = data;
    dataSource.push(data);
    this.setState({ dataSource });
  };

  // 行编辑 flag: true 编辑，flag: false 取消
  edit = (index, flag, record) => {
    let { dataSource, dataCache } = this.state;
    const { lineEvent } = this.props;
    const finalIndex = this.getFinalIndex(index);
    if (flag) {
      dataCache[finalIndex] = { ...record };
      dataSource[finalIndex].isEdit = true;
    } else {
      if (!!dataCache[finalIndex]) {
        dataSource[finalIndex] = dataCache[finalIndex];
      } else {
        dataSource.splice(finalIndex, 1);
        dataCache.splice(finalIndex, 1);
      }
      lineEvent && lineEvent();
    }
    this.setState({ dataSource, dataCache });
  };

  validateRow = (index, record) => {
    let requireItem = this.state.columns.map(item => {
      console.log(item);
    });
  };

  // 行保存
  save = (index, record) => {
    this.validateRow(index, record);
    const { dataSource } = this.state;
    const { lineSave } = this.props;
    dataSource[this.getFinalIndex(index)].isEdit = false;
    this.setState({ dataSource });

    // todo lineSave
    !!lineSave && lineSave(dataSource);
  };

  // 行删除
  delete = index => {
    const { dataSource, dataCache } = this.state;
    const { lineEvent } = this.props;
    const finalIndex = this.getFinalIndex(index);
    dataSource.splice(finalIndex, 1);
    dataCache.splice(finalIndex, 1);
    this.setState({ dataSource });
    message.success('删除成功');
    lineEvent && lineEvent();
  };

  render() {
    const { columns, scroll, dataSource, pagination } = this.state;
    const { children } = this.props;
    return (
      <Spin size="large" spinning={false}>
        <div style={{ margin: '15px 0' }}>
          {!!children ? (
            children
          ) : (
            <Button onClick={this.addRowTable}>
              <Icon type="plus" />
              {this.$t('expense.new.invoice.line')}
            </Button>
          )}
        </div>
        <Table
          rowKey="key"
          columns={columns}
          loading={false}
          dataSource={dataSource}
          pagination={pagination}
          size="small"
          scroll={{ x: scroll }}
        />
      </Spin>
    );
  }
}

EditTable.propTypes = {
  title: PropTypes.string, //标题
  children: PropTypes.any, //标题
  handleEvent: PropTypes.func, //事件回调
  refresh: PropTypes.any, //刷新标记
};

EditTable.defaultProps = {
  lineSave: false,
  children: false,
};

function mapStateToProps(state) {
  return {
    language: state.languages,
    company: state.user.company,
  };
}

//const WrappedEditTable = Form.create()(EditTable)

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(EditTable);

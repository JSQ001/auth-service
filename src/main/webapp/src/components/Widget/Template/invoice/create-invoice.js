import React from 'react';
import { connect } from 'dva';
import {
  DatePicker,
  Form,
  Button,
  Input,
  Select,
  message,
  Popover,
  Spin,
  Col,
  Tooltip,
  Icon,
  InputNumber,
  Divider,
  Popconfirm,
} from 'antd';
const FormItem = Form.Item;
import AddInvoiceLine from 'components/Widget/Template/invoice/edit-table';
import service from './service';
import PropTypes from 'prop-types';
import httpFetch from 'share/httpFetch';
import config from 'config';
import invoiceImg from 'images/expense/invoice-info.png';
import invoiceImgEn from 'images/expense/invoice-info-en.png';

class CreateInvoice extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      moreLoading: false,
      loading: false,
      headInfo: [],
      buyerInfo: [],
      sellerInfo: [],
      dataSource: [],
      returnData: [],
      invoiceHeadConfig: [
        {
          label: '开票日期',
          dataIndex: 'invoiceDate',
          type: 'date',
          placeholder: '请选择日期',
        },
        {
          label: '发票代码',
          dataIndex: 'invoiceCode',
          type: 'input',
          onBlur: this.validateCode,
          validateStatus: true,
          hasFeedback: true,
          help: true,
        },
        {
          label: '发票号码',
          dataIndex: 'invoiceNo',
          type: 'input',
          onBlur: this.validateCode,
          validateStatus: true,
          hasFeedback: true,
          help: true,
        },
        { label: '设备编码', dataIndex: 'machineNo', type: 'input' },
        {
          label: '校验码',
          dataIndex: 'checkCode',
          type: 'input',
          placeholder: '请输入校验码后6位',
          rules: [
            {
              pattern: /^[A-Za-z0-9]{6}$/g,
              message: '请输入校验码后6位',
            },
          ],
        },
        {
          label: '币种',
          dataIndex: 'currencyCode',
          type: 'select',
          options: [],
          disabled: true,
          defaultValue: props.currency,
          placeholder: '请选择',
          method: service.getCurrency,
          onChange: this.handleSetRate,
          labelKey: 'currency.currencyName',
          valueKey: 'currency',
        },
        {
          label: '价税合计',
          dataIndex: 'totalAmount',
          type: 'number',
          onChange: value => {
            this.setState({ totalAmount: value }, this.amountCalculate);
          },
        },
        {
          label: '税额',
          dataIndex: 'taxTotalAmount',
          type: 'number',
          onChange: value => {
            this.setState({ taxTotalAmount: value }, this.amountCalculate);
          },
        },
        /*     {
          label: '汇率',
          dataIndex: 'exchangeRate',
          type: 'input',
          disabled: true
        },*/
        {
          label: '金额合计',
          dataIndex: 'invoiceAmount',
          type: 'input',
          disabled: true,
          placeholder: '自动算出',
          tips: '输入价税合计&税额后自动算出',
          rules: [
            {
              pattern: /^[\d]*[.]?[\d]*$/,
              message: '税额不得大于税价合计',
            },
          ],
        },
        { label: '备注', dataIndex: 'remark', type: 'textArea', colSpan: 24 },
      ],
      buyerConfig: [
        { label: '购买方', dataIndex: 'buyerName', type: 'input' },
        { label: '纳税识别人编号', dataIndex: 'buyerTaxNo', type: 'input' },
        { label: '地址/电话', dataIndex: 'buyerAddPh', type: 'input' },
        { label: '开户行/账号', dataIndex: 'buyerAccount', type: 'input' },
      ],
      sellerConfig: [
        { label: '销售方', dataIndex: 'salerName', type: 'input' },
        { label: '纳税识别人编号', dataIndex: 'salerTaxNo', type: 'input' },
        { label: '地址/电话', dataIndex: 'salerAddPh', type: 'input' },
        { label: '开户行/账号', dataIndex: 'salerAccount', type: 'input' },
      ],
      editTableRefresh: new Date().getTime(),
      columns: [],
      columnConfig: [
        {
          title: '序号',
          dataIndex: 'invoiceLineNum',
          render: (value, record, index) => {
            record.invoiceLineNum = index + 1;
            return index + 1;
          },
          type: 'sequence',
          align: 'center',
          width: '45px',
        },
        {
          title: '货物或应税劳务、服务名称',
          dataIndex: 'goodsName',
          align: 'center',
          type: 'input',
          width: '120px',
        },
        {
          title: '规格型号',
          type: 'input',
          dataIndex: 'specificationModel',
          align: 'center',
        },
        {
          title: '单位',
          dataIndex: 'unit',
          align: 'center',
          type: 'input',
        },
        {
          title: '数量',
          dataIndex: 'num',
          align: 'center',
          type: 'inputNumber',
        },
        {
          title: '单价',
          type: 'inputNumber',
          dataIndex: 'unitPrice',
          align: 'center',
        },
        {
          title: '金额',
          type: 'inputNumber',
          dataIndex: 'detailAmount',
          align: 'center',
        },
        {
          title: '税率',
          type: 'select',
          dataIndex: 'taxRate',
          align: 'center',
        },
        {
          title: '税额',
          type: 'inputNumber',
          dataIndex: 'taxAmount',
          align: 'center',
          /*override: {
            precision: 3
          }*/
        },
        {
          title: '操作',
          dataIndex: 'operation',
          align: 'center',
          type: 'operation',
          width: '95px',
        },
      ],
    };
  }

  componentDidMount() {
    this.getTax();
    this.getTemplate();
  }

  getTax = () => {
    service
      .getTaxRate()
      .then(res => {
        let { columnConfig } = this.state;
        let data = res.data.map(item => item.value.split('%')[0]);
        data = data.sort((a, b) => a - b);
        let result = data.map(item => {
          return {
            key: item + '%',
            label: item + '%',
          };
        });
        columnConfig[7].options = result;
        this.setState({
          taxRate: result,
          columnConfig,
        });
      })
      .catch(err => message.error(err.response.data.message));
  };

  // 获取模板
  getTemplate = () => {
    const { invoiceTypeId, handleCancel } = this.props;
    const {
      invoiceHeadConfig,
      headInfo,
      columnConfig,
      buyerInfo,
      sellerInfo,
      buyerConfig,
      sellerConfig,
    } = this.state;

    httpFetch
      .get(`${config.expenseUrl}/api/invoice/type/mould/query/${invoiceTypeId}`)
      .then(res => {
        const headData = res.data.invoiceTypeMouldHeadColumn;
        const lineData = res.data.invoiceTypeMouldLineColumn;
        if (!headData || !lineData) {
          handleCancel && setTimeout(handleCancel, 2000);
          return message.warning('该发票类型没有发票模板，请添加模板！');
        }
        invoiceHeadConfig.map(item => {
          if (headData[item.dataIndex] !== 'DISABLED') {
            headInfo.push({
              ...item,
              ...{ required: headData[item.dataIndex] === 'REQUIRED' },
            });
          }
        });
        buyerConfig.map(item => {
          if (headData[item.dataIndex] !== 'DISABLED') {
            buyerInfo.push({ ...item, ...{ required: headData[item.dataIndex] === 'REQUIRED' } });
          }
        });
        sellerConfig.map(item => {
          if (headData[item.dataIndex] !== 'DISABLED') {
            sellerInfo.push({ ...item, ...{ required: headData[item.dataIndex] === 'REQUIRED' } });
          }
        });

        let columns = columnConfig
          .filter(item => lineData[item.dataIndex] !== 'DISABLED')
          .map(item => {
            return {
              ...item,
              required: lineData[item.dataIndex] === 'REQUIRED',
            };
          });
        this.setState({
          headInfo,
          columns,
          spinLoading: false,
        });
      });
  };

  // 发票头 金额合计 计算
  amountCalculate = () => {
    if (!this.state.isShowInvoiceAmount) return;
    const { totalAmount, taxTotalAmount } = this.state;
    if (this.isNumber(totalAmount) && this.isNumber(taxTotalAmount)) {
      let amount = totalAmount - taxTotalAmount;
      if (amount < 0) {
        amount = '税额不得大于税价合计';
      }
      this.props.form.setFieldsValue({ invoiceAmount: amount });
    } else {
      this.props.form.setFieldsValue({ invoiceAmount: undefined });
    }
  };

  //币种设置税率
  handleSetRate = value => {
    let { headInfo } = this.state;
    let currency = headInfo.find(item => item.dataIndex === 'currencyCode') || [];
    let rate = (currency.options.find(item => item.currency === value) || {}).rate;
    this.props.form.setFieldsValue({ exchangeRate: rate });
  };

  // 发票头 发票号码、代码校验
  validateCode = () => {
    let code = this.props.form.getFieldValue('invoiceCode');
    let no = this.props.form.getFieldValue('invoiceNo');
    if (code && no) {
      this.setState({ validateStatus: 'validating' });
      service
        .validateInvoiceCode({ invoiceCode: code, invoiceNo: no })
        .then(() => {
          this.setState({ validateStatus: 'success', helpMessage: undefined });
        })
        .catch(err => {
          this.setState({ validateStatus: 'error', helpMessage: '请重新输入' });
          message.error(err.response.data.message);
        });
    }
  };

  handleCloseInvoice = () => {
    this.setState({ showAddInvoice: false });
  };

  onCancel = () => {
    this.props.onCancel();
  };

  getLabel(key, item) {
    let str = key.split('.');
    return `${item[str[0]]}${str.length > 1 && '-' + item[str[1]]}`;
  }

  getOptions = (item, type, index) => {
    let params = this.state[type];
    if (!item.options.length) {
      item
        .method(item.params)
        .then(res => {
          item.options = res.data.map(i => {
            i.key = i[item.valueKey];
            i.label = this.getLabel(item.labelKey, i);
            return i;
          });
          params[index] = item;
          this.setState({ [type]: params });
        })
        .catch(err => message.error(err.response.data.message));
    }
  };

  getFields = (item, type, index, flag, isHead) => {
    let style = isHead ? { width: flag ? '93%' : '86%' } : {};
    switch (item.type) {
      case 'input':
        return (
          <Input
            disabled={item.disabled}
            style={style}
            placeholder={item.placeholder || '请输入'}
            onBlur={item.onBlur}
          />
        );
      case 'number':
        return <InputNumber min={0} placeholder="请输入" onChange={item.onChange} />;
      case 'date':
        return <DatePicker getCalendarContainer={trigger => trigger.parentNode} />;
      case 'select':
        return (
          <Select
            style={style}
            disabled={item.disabled}
            onChange={item.onChange ? item.onChange : () => {}}
            onFocus={() => this.getOptions(item, type, index)}
            placeholder={this.$t('common.please.select')}
            getPopupContainer={trigger => trigger.parentNode}
          >
            {item.options.map(item => {
              return <Select.Option key={item.key}>{item.label}</Select.Option>;
            })}
          </Select>
        );
      case 'textArea':
        return <Input.TextArea style={style} placeholder="请输入" />;
      default:
        return <Input style={style} placeholder="请输入" />;
    }
  };

  // 底部 再录一张  重新初始化
  handleAddMore = () => {
    this.setState({ moreLoading: true });
    this.save(() => {
      this.props.form.resetFields();
      console.log(this.state.returnData);
      this.setState({ dataSource: [], moreLoading: false });
    });
  };

  save = callback => {
    this.props.form.validateFields((err, value) => {
      if (err) {
        this.setState({ loading: false, moreLoading: false });
        return;
      }
      const { company, invoiceTypeId, saveUrl } = this.props;
      let { returnData } = this.state;

      let invoiceLineList = this.editTable.state.dataSource.map(item => {
        let obj = {
          ...item,
          invoiceLineNum: item.key + 1,
        };
        item.num && (item.num = Number(item.num));
        return obj;
      });
      let params = {
        invoiceHead: {
          ...value,
          setOfBooksId: company.setOfBooksId,
          tenantId: company.tenantId,
          invoiceTypeId,
          invoiceDate: value.invoiceDate.format(),
          createdMethod: 'BY_HAND',
          fromBook: true,
        },
        invoiceLineList,
      };

      let url = saveUrl ? saveUrl : 'api/invoice/head/insert/invoice';
      httpFetch
        .post(`${config.expenseUrl}/${url}`, params)
        .then(res => {
          message.success('保存成功！');
          console.log(res.data);
          returnData.push({ ...res.data });
          this.setState({ returnData }, callback);
        })
        .catch(err => {
          message.error(err.response && err.response.data.message);
          this.setState({ saveLoading: false });
        });
    });
  };

  handleSave = e => {
    e.preventDefault();
    const { returnData } = this.state;
    console.log(returnData);
    this.save(() => {
      this.props.saveSuccess(returnData);
    });
  };

  // 表格行校验
  validateRow = data => {
    const { rowColumns } = this.state;
    let notEmpty = rowColumns.every(item => {
      return item.className ? data[item.dataIndex] || String(data[item.dataIndex]) === '0' : true;
    });
    if (notEmpty) {
      const flag = data.hasOwnProperty('detailAmount') && data.hasOwnProperty('taxRate');
      if (flag) {
        let taxAmount = Number(data.taxAmount) || 0;
        let rate = data.taxRate.split('%')[0] / 100;
        let result =
          (data.detailAmount * rate).toFixed(2) >= taxAmount && data.detailAmount >= taxAmount;
        if (result) {
          return true;
        } else {
          message.warning(`发票行 ${data.invoiceLineNum} 行税额不得大于金额*税率，请检查并修改！`);
          return false;
        }
      }
      return true;
    } else {
      message.warning('必输字段不可为空！');
      return false;
    }
  };

  /*
  *  行事件处理,
  *
  *  计算税率
  * */
  handleLineEvent = (key, value, index) => {
    let { dataSource } = this.editTable.state;
    let refresh = false;
    switch (key) {
      case 'detailAmount':
        {
          if (!!dataSource[index].taxRate) {
            refresh = true;
            dataSource[index].taxAmount =
              (value * dataSource[index].taxRate.replace('%', '')) / 100;
          }
        }
        break;
      case 'taxRate':
        {
          if (!!dataSource[index].detailAmount) {
            refresh = true;
            dataSource[index].taxAmount =
              (value.replace('%', '') / 100) * dataSource[index].detailAmount;
          }
        }
        break;
      case 'unitPrice':
        {
          if (dataSource[index].num) {
            refresh = true;
            dataSource[index].detailAmount = Number(dataSource[index].num) * Number(value);
          }
        }
        break;
      case 'num':
        {
          if (dataSource[index].unitPrice) {
            refresh = true;
            dataSource[index].detailAmount = Number(dataSource[index].unitPrice) * Number(value);
          }
        }
        break;
    }
    if (refresh) {
      this.setState({
        editTableRefresh: new Date().getTime(),
      });
    }
  };

  render() {
    const { getFieldDecorator, getFieldValue } = this.props.form;
    const {
      headInfo,
      buyerInfo,
      sellerInfo,
      spinLoading,
      showAddInvoice,
      showInvoiceDetail,
      invoiceId,
      loading,
      columns,
      dataSource,
      moreLoading,
      editTableRefresh,
    } = this.state;
    const { record, headInfoStyle, buyerInfoStyle, currency } = this.props;
    const headLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 16, offset: 0 },
    };
    const buyerLayout = {
      labelCol: {
        span: 4,
      },
      wrapperCol: {
        span: 18,
      },
    };
    return (
      <Spin size="large" spinning={spinLoading}>
        <div className="create-invoice-head">
          <Form onSubmit={this.handleSave}>
            {headInfo.length && (
              <div className="common-item-title clear">
                发票头信息
                <Popover
                  trigger="click"
                  placement="bottomRight"
                  content={
                    <img
                      style={{ width: '600px' }}
                      src={this.props.language.local === 'zh_cn' ? invoiceImg : invoiceImgEn}
                    />
                  }
                >
                  <a style={{ float: 'right' }}>发票填写说明</a>
                </Popover>
              </div>
            )}
            <div className="head-info" style={headInfoStyle}>
              {headInfo.map((item, index) => {
                let flag = index % 2 === 0;
                let style = flag
                  ? {
                      paddingLeft: 39,
                    }
                  : {};
                return (
                  <Col span={12} style={style} key={item.dataIndex} className="invoice-col">
                    <FormItem
                      {...headLayout}
                      key={item.dataIndex}
                      hasFeedback={item.hasFeedback}
                      label={
                        item.tips ? (
                          <span>
                            {item.label}&nbsp;
                            <Tooltip title={item.tips}>
                              <Icon type="question-circle-o" />
                            </Tooltip>
                          </span>
                        ) : (
                          item.label
                        )
                      }
                    >
                      {getFieldDecorator(item.dataIndex, {
                        initialValue: item.defaultValue || '',
                        rules: [
                          {
                            required: item.required,
                            message: item.placeholder || '请输入',
                          },
                          ...(item.rules || []),
                        ],
                      })(this.getFields(item, 'headInfo', index, flag, true))}
                    </FormItem>
                  </Col>
                );
              })}
            </div>
            <div className="buyer-info" style={buyerInfoStyle}>
              {!!buyerInfo.length && <div className="common-item-title clear">购方信息</div>}
              {buyerInfo.map((item, index) => {
                return (
                  <Col span={24} key={item.dataIndex} className="invoice-col">
                    <FormItem
                      {...buyerLayout}
                      key={item.dataIndex}
                      hasFeedback={item.hasFeedback}
                      label={
                        item.tips ? (
                          <span>
                            {item.label}&nbsp;
                            <Tooltip title={item.tips}>
                              <Icon type="question-circle-o" />
                            </Tooltip>
                          </span>
                        ) : (
                          item.label
                        )
                      }
                    >
                      {getFieldDecorator(item.dataIndex, {
                        rules: [
                          {
                            required: item.required,
                            message: item.placeholder || '请输入',
                          },
                          ...(item.rules || []),
                        ],
                      })(this.getFields(item, 'buyerInfo', index))}
                    </FormItem>
                  </Col>
                );
              })}
            </div>
            <div className="sellerInfo-info" style={buyerInfoStyle}>
              {!!buyerInfo.length && <div className="common-item-title clear">销方信息</div>}
              {sellerInfo.map((item, index) => {
                return (
                  <Col span={24} key={item.dataIndex} className="invoice-col">
                    <FormItem
                      {...buyerLayout}
                      key={item.dataIndex}
                      hasFeedback={item.hasFeedback}
                      label={
                        item.tips ? (
                          <span>
                            {item.label}&nbsp;
                            <Tooltip title={item.tips}>
                              <Icon type="question-circle-o" />
                            </Tooltip>
                          </span>
                        ) : (
                          item.label
                        )
                      }
                    >
                      {getFieldDecorator(item.dataIndex, {
                        rules: [
                          {
                            required: item.required,
                            message: item.placeholder || '请输入',
                          },
                          ...(item.rules || []),
                        ],
                      })(this.getFields(item, 'sellerInfo', index))}
                    </FormItem>
                  </Col>
                );
              })}
            </div>

            <div className="slide-footer" style={{ marginBottom: '-10%' }}>
              <Button type="primary" htmlType="submit" loading={loading}>
                {this.$t({ id: 'common.save' })}
              </Button>
              <Button onClick={this.handleAddMore} loading={moreLoading}>
                {this.$t('expense.wallet.invoice.again') /* 再录一张 */}
              </Button>
              <Button onClick={this.onCancel}>{this.$t({ id: 'common.cancel' })}</Button>
            </div>
          </Form>
        </div>
        <div className="create-invoice-line" style={{ marginBottom: '10%' }}>
          <div className="common-item-title clear">发票行信息</div>
          <AddInvoiceLine
            refresh={editTableRefresh}
            ref={ref => (this.editTable = ref && ref.wrappedInstance)}
            columns={columns}
            dataSource={dataSource}
            handleEvent={this.handleLineEvent}
            onCancel={this.handleCloseInvoice}
          />
        </div>
      </Spin>
    );
  }
}

function mapStateToProps(state) {
  return {
    company: state.user.company,
    profile: state.user.proFile,
    user: state.user.currentUser,
    language: state.languages,
  };
}

CreateInvoice.propTypes = {
  record: PropTypes.object, //编辑时的发票信息
  invoiceTypeId: PropTypes.string, //发票类型id
};

CreateInvoice.defaultProps = {
  title: {},
  headInfoStyle: {
    // marginBottom: 100
  },
  buyerInfoStyle: {},
};

const WrappedCreateInvoice = Form.create()(CreateInvoice);

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(WrappedCreateInvoice);

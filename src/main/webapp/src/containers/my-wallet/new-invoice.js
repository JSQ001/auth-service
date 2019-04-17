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
import 'styles/my-wallet/new-invoice.scss';
import service from './my-wallet-service';

const FormItem = Form.Item;
const Option = Select.Option;
const last = new Date();
let timer;

class NewInvoice extends Component {
  constructor(props) {
    super(props);
    this.state = {
      invoiceHeadConfig: [
        {
          label: this.$t('expense.wallet.invoiceDate') /* 开票日期 */,
          dataIndex: 'invoiceDate',
          type: 'date',
          placeholder: this.$t('expense.please.select.date') /* 请选择日期 */,
        },
        {
          label: this.$t('expense.wallet.invoiceCode') /* 发票代码 */,
          dataIndex: 'invoiceCode',
          type: 'input',
          validateStatus: 0,
          hasFeedback: true,
          help: 0,
          rules: [
            {
              validator: (rule, value, callback) => this.validateDefine(0, value, callback),
            },
          ],
        },
        {
          label: this.$t('expense.wallet.invoiceNo') /* 发票号码 */,
          dataIndex: 'invoiceNo',
          type: 'input',
          validateStatus: 1,
          hasFeedback: true,
          help: 1,
          rules: [
            {
              validator: (rule, value, callback) => this.validateDefine(1, value, callback),
            },
          ],
        },
        {
          label: this.$t('expense.wallet.machineNo') /* 设备编码 */,
          dataIndex: 'machineNo',
          type: 'input',
        },
        {
          label: this.$t('expense.invoice.check.code') /* 校验码 */,
          dataIndex: 'checkCode',
          type: 'input',
          placeholder: this.$t('expense.invoice.input.code') /* 请输入校验码后6位 */,
          rules: [
            {
              pattern: /^[A-Za-z0-9]{6}$/g,
              message: this.$t('expense.invoice.input.code') /* 请输入校验码后6位 */,
            },
          ],
        },
        {
          label: this.$t('expense.policy.currencyName') /* 币种 */,
          dataIndex: 'currencyCode',
          type: 'select',
        },
        {
          label: this.$t('expense.wallet.totalAmount') /* 价税合计 */,
          dataIndex: 'totalAmount',
          type: 'number',
          onChange: value => {
            this.setState({ totalAmount: value }, this.amountCalculate);
          },
        },
        {
          label: this.$t('common.tax') /* 税额 */,
          dataIndex: 'taxTotalAmount',
          type: 'number',
          onChange: value => {
            this.setState({ taxTotalAmount: value }, this.amountCalculate);
          },
        },
        {
          label: this.$t('expense.invoice.amount.without.tax') /* 金额合计 */,
          dataIndex: 'invoiceAmount',
          type: 'input',
          disabled: true,
          placeholder: this.$t('expense.wallet.auto.tips'),
          tips: this.$t('expense.wallet.autoCalculate') /* 输入价税合计&税额后自动算出 */,
          rules: [
            {
              pattern: /^[\d]*[.]?[\d]*$/,
              message: this.$t('expense.wallet.tax.notMore.amount') /* 税额不得大于税价合计 */,
            },
          ],
        },
        { label: this.$t('common.remark'), dataIndex: 'remark', type: 'textArea', colSpan: 24 },
      ],
      purchaserConfig: [
        {
          label: this.$t('expense.invoice.buyer') /* 购买方 */,
          dataIndex: 'buyerName',
          type: 'input',
        },
        {
          label: this.$t('expense.wallet.tax.number') /* 纳税识别人编号 */,
          dataIndex: 'buyerTaxNo',
          type: 'input',
        },
        {
          label: this.$t('expense.invoice.address.phone') /* 地址/电话 */,
          dataIndex: 'buyerAddPh',
          type: 'input',
        },
        {
          label: this.$t('expense.wallet.saleAccount') /* 开户行/账号 */,
          dataIndex: 'buyerAccount',
          type: 'input',
        },
      ],
      sellerConfig: [
        {
          label: this.$t('expense.invoice.seller') /* 销售方 */,
          dataIndex: 'salerName',
          type: 'input',
        },
        {
          label: this.$t('expense.wallet.tax.number') /* 纳税识别人编号 */,
          dataIndex: 'salerTaxNo',
          type: 'input',
        },
        {
          label: this.$t('expense.invoice.address.phone') /* 地址/电话 */,
          dataIndex: 'salerAddPh',
          type: 'input',
        },
        {
          label: this.$t('expense.wallet.saleAccount') /* 开户行/账号 */,
          dataIndex: 'salerAccount',
          type: 'input',
        },
      ],
      rowColumnConfig: [
        {
          title: this.$t('common.sequence') /* 序号 */,
          dataIndex: 'invoiceLineNum',
          render: (value, record, index) => {
            record.invoiceLineNum = index + 1;
            return index + 1;
          },
          align: 'center',
          width: '45px',
        },
        {
          title: this.$t('expense.invoice.goods.name') /* 货物或应税劳务、服务名称 */,
          dataIndex: 'goodsName',
          align: 'center',
          render: (value, record, index) => {
            return this.tableRender(value, record, index, 'goodsName', 'input');
          },
          width: '120px',
        },
        {
          title: this.$t('expense.invoice.vehicle.type') /* 规格型号 */,
          dataIndex: 'specificationModel',
          align: 'center',
          render: (value, record, index) => {
            return this.tableRender(value, record, index, 'specificationModel', 'input');
          },
        },
        {
          title: this.$t('common.unit') /* 单位 */,
          dataIndex: 'unit',
          align: 'center',
          render: (value, record, index) => {
            return this.tableRender(value, record, index, 'unit', 'input');
          },
        },
        {
          title: this.$t('common.number') /* 数量 */,
          dataIndex: 'num',
          align: 'center',
          render: (value, record, index) => {
            return this.tableRender(value, record, index, 'num', 'number');
          },
        },
        {
          title: this.$t('common.price') /* 单价 */,
          dataIndex: 'unitPrice',
          align: 'center',
          render: (value, record, index) => {
            return this.tableRender(value, record, index, 'unitPrice', 'number');
          },
        },
        {
          title: this.$t('common.amount') /* 金额 */,
          dataIndex: 'detailAmount',
          align: 'center',
          render: (value, record, index) => {
            return this.tableRender(value, record, index, 'detailAmount', 'auto');
          },
        },
        {
          title: this.$t('expense.invoice.tax.rate') /* 税率 */,
          dataIndex: 'taxRate',
          align: 'center',
          render: (value, record, index) => {
            return this.tableRender(value, record, index, 'taxRate', 'select');
          },
        },
        {
          title: this.$t('common.tax') /* 税额 */,
          dataIndex: 'taxAmount',
          align: 'center',
          render: (value, record, index) => {
            return this.tableRender(value, record, index, 'taxAmount', 'auto');
          },
        },
        {
          title: this.$t('common.operation') /* 操作 */,
          dataIndex: 'operation',
          align: 'center',
          render: (value, record, index) => {
            if (record.isEdit) {
              return (
                <>
                  <a onClick={() => this.rowSave(index, record)} title={this.$t('common.save')}>
                    {this.$t('common.save')}
                  </a>
                  <Divider type="vertical" />
                  <a
                    onClick={() => this.rowEdit(index, false, record)}
                    title={this.$t('common.cancel')}
                  >
                    {this.$t('common.cancel')}
                  </a>
                </>
              );
            } else {
              return (
                <>
                  <a
                    onClick={() => this.rowEdit(index, true, record)}
                    title={this.$t('common.edit')}
                  >
                    {this.$t('common.edit')}
                  </a>
                  <Divider type="vertical" />
                  <Popconfirm
                    title={this.$t('common.confirm.delete')}
                    onConfirm={() => this.rowDelete(index)}
                  >
                    <a title={this.$t('common.delete')}>{this.$t('common.delete')}</a>
                  </Popconfirm>
                </>
              );
            }
          },
          width: '95px',
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
      taxRate: [],
      currencyList: [],
      invoiceHeadList: [],
      purchaserList: [],
      sellerList: [],
      rowColumns: [],
      isShowInvoiceAmount: false,
      isShowTaxAmount: false,
      isShowNum: false,
      validateStatus: [],
      helpMessage: [],
      invoiceTypeId: undefined,
      moreLoading: false,
      codeRule: [], // 代码位数
      numberRule: [], // 号码位数
      codeValidate: undefined, // 代码位数校验状态
      numberValidate: undefined, // 号码校验状态
    };
  }

  componentDidMount() {
    this.getSelected(this.props.invoiceTypeId);
    this.getParams();
  }

  // 发票类型 选择
  invoiceChange = value => {
    const { changeTitle } = this.props;
    this.getSelected(value.key);
    changeTitle({ id: value.key, name: value.label });
  };

  // 获取选中的模板
  getSelected = invoiceTypeId => {
    const { menuList } = this.props;
    this.setState({ spinLoading: true, invoiceTypeId });
    this.resetTemplate();
    menuList.some(item => {
      if (item.id === invoiceTypeId) {
        this.getTemplate(item);
        return true;
      }
    });
  };

  // 生成模板
  getTemplate = data => {
    const { invoiceHeadConfig, purchaserConfig, sellerConfig, rowColumnConfig } = this.state;
    const invoiceHeadList = [];

    const purchaserList = [];

    const sellerList = [];

    const rowColumns = [];

    let isShowInvoiceAmount;

    let isShowTaxAmount;

    let isShowNum;

    const headData = data.invoiceTypeMouldHeadColumn;
    const rowData = data.invoiceTypeMouldLineColumn;

    const codeRule = data.invoiceCodeLength ? data.invoiceCodeLength.split(',') : [];
    const numberRule = data.invoiceNumberLength ? data.invoiceNumberLength.split(',') : [];

    if (!headData || !rowData) {
      this.clearTemplate();
      return message.warning(this.$t('expense.wallet.noTemplate')); // 该发票类型没有发票模板！
    }
    invoiceHeadConfig.map(item => {
      if (headData[item.dataIndex] !== 'DISABLED') {
        item.dataIndex === 'invoiceAmount' && (isShowInvoiceAmount = true);
        invoiceHeadList.push({
          ...item,
          ...{ required: headData[item.dataIndex] === 'REQUIRED' },
        });
      }
    });
    purchaserConfig.map(item => {
      if (headData[item.dataIndex] !== 'DISABLED') {
        purchaserList.push({
          ...item,
          ...{ required: headData[item.dataIndex] === 'REQUIRED' },
        });
      }
    });
    sellerConfig.map(item => {
      if (headData[item.dataIndex] !== 'DISABLED') {
        sellerList.push({ ...item, ...{ required: headData[item.dataIndex] === 'REQUIRED' } });
      }
    });
    rowColumnConfig.map(item => {
      if (rowData[item.dataIndex] !== 'DISABLED') {
        item.dataIndex === 'taxAmount' && (isShowTaxAmount = true);
        item.dataIndex === 'num' && (isShowNum = true);
        rowColumns.push({
          ...item,
          ...{ className: rowData[item.dataIndex] === 'REQUIRED' ? 'required' : '' },
        });
      }
    });
    this.setState({
      invoiceHeadList,
      purchaserList,
      sellerList,
      rowColumns,
      isShowInvoiceAmount,
      isShowTaxAmount,
      isShowNum,
      spinLoading: false,
      codeRule,
      numberRule,
    });
  };

  // 清空模板
  clearTemplate = () => {
    this.resetTemplate();
    this.setState({
      invoiceHeadList: [],
      purchaserList: [],
      sellerList: [],
      rowColumns: [],
    });
  };

  // 重新初始化
  resetTemplate = () => {
    this.props.form.resetFields();
    this.setState({
      dataSource: [],
      dataCache: [],
      totalAmount: undefined,
      taxTotalAmount: undefined,
      validateStatus: [],
      helpMessage: [],
    });
  };

  // 获取币种、税率
  getParams = () => {
    service
      .getCurrencyType()
      .then(res => this.setState({ currencyList: res.data }))
      .catch(err => message.error(err.response.data.message));
    service
      .getTaxRate()
      .then(res => {
        let data = res.data.map(item => item.value.split('%')[0]);
        data = data.sort((a, b) => a - b);
        const result = data.map(item => `${item}%`);
        this.setState({ taxRate: result });
      })
      .catch(err => message.error(err.response.data.message));
  };

  // 控件类型
  itemType = item => {
    switch (item.type) {
      case 'input':
        return (
          <Input
            disabled={item.disabled}
            placeholder={item.placeholder || this.$t('common.please.enter')}
          />
        );
      case 'number':
        return (
          <InputNumber
            min={0}
            placeholder={this.$t('common.please.enter')}
            onChange={item.onChange}
          />
        );
      case 'date':
        return (
          <DatePicker
            getCalendarContainer={trigger => trigger.parentNode}
            placeholder={item.placeholder}
          />
        );
      case 'select':
        return (
          <Select
            placeholder={this.$t('common.please.select')}
            getPopupContainer={trigger => trigger.parentNode}
          >
            {this.state.currencyList.map(item => {
              return (
                <Option key={item.companyCurrencyOid} value={item.currency}>
                  {`${item.currency}-${item.currencyName}`}
                </Option>
              );
            })}
          </Select>
        );
      case 'textArea':
        return <Input.TextArea placeholder={this.$t('common.please.enter')} />;
      default:
        return <Input placeholder={this.$t('common.please.enter')} />;
    }
  };

  // 发票头 金额合计 计算
  amountCalculate = () => {
    if (!this.state.isShowInvoiceAmount) return;
    const { totalAmount, taxTotalAmount } = this.state;
    if (this.isNumber(totalAmount) && this.isNumber(taxTotalAmount)) {
      let amount = totalAmount - taxTotalAmount;
      if (amount < 0) {
        amount = this.$t('expense.wallet.tax.notMore.amount'); // 税额不得大于税价合计
      }
      this.props.form.setFieldsValue({ invoiceAmount: amount });
    } else {
      this.props.form.setFieldsValue({ invoiceAmount: undefined });
    }
  };

  // 发票 号码、代码 长度校验 正则校验
  renderRules = (flag, value) => {
    const { codeRule, numberRule } = this.state;
    // 先动态生成字符串，再将字符串转化为正则表达式
    const codeStr = codeRule.map(item => {
      return `^\\w{${item}}$`;
    });
    const numberStr = numberRule.map(item => {
      return `^\\w{${item}}$`;
    });
    if (flag === 0) {
      const result = new RegExp(codeStr.join('|')).test(value);
      if (!value || !result) {
        this.setState({ codeValidate: true });
        return codeRule.length
          ? this.$t('expense.wallet.code.num', {
              no: codeRule.join(),
            }) /* 请输入{no}位数的发票代码 */
          : this.$t('common.please.enter');
      }
      this.setState({ codeValidate: false });
    } else {
      const result = new RegExp(numberStr.join('|')).test(value);
      if (!value || !result) {
        this.setState({ numberValidate: true });
        return numberRule.length
          ? this.$t('expense.wallet.number.num', {
              no: numberRule.join(),
            }) /* 请输入{no}位数的发票号码 */
          : this.$t('common.please.enter');
      }
      this.setState({ numberValidate: false });
    }
  };

  // 发票头 发票号码、代码校验  函数节流
  validateDefine = (flag, value, callback) => {
    const code = this.props.form.getFieldValue('invoiceCode');
    const no = this.props.form.getFieldValue('invoiceNo');
    const { validateStatus, helpMessage } = this.state;
    const now = new Date();
    const result = this.renderRules(flag, value);
    if (result) {
      validateStatus[flag] = 'error';
      helpMessage[flag] = result;
    } else {
      validateStatus[flag] = 'success';
      helpMessage[flag] = undefined;
    }
    this.setState({ validateStatus, helpMessage });

    if (now - last > 500) {
      // 函数节流
      clearTimeout(timer);
      if (code && no) {
        timer = setTimeout(() => {
          this.validateCode(code, no);
          callback();
        }, 600);
      } else {
        callback();
      }
    } else {
      callback();
    }
  };

  // 发票头 发票号码、代码校验 调用接口
  validateCode = (code, no) => {
    this.setValidateStatus('validating');
    service
      .validateInvoiceCode({ invoiceCode: code, invoiceNo: no })
      .then(() => {
        this.setValidateStatus('success');
      })
      .catch(err => {
        this.setValidateStatus('error', this.$t('expense.wallet.input.again') /* 请重新输入 */);
        message.error(err.response.data.message);
      });
  };

  // 设置校验状态
  setValidateStatus = (status, text) => {
    const { codeValidate, numberValidate, validateStatus, helpMessage } = this.state;
    this.setState({
      validateStatus: [
        codeValidate ? validateStatus[0] : status,
        numberValidate ? validateStatus[1] : status,
      ],
      helpMessage: [codeValidate ? helpMessage[0] : text, numberValidate ? helpMessage[1] : text],
    });
  };

  // 底部 保存
  onSubmit = e => {
    e.preventDefault();
    this.handleSubmit().catch(() => {});
  };

  // 保存 调用接口
  handleSubmit = isClose => {
    return new Promise((resolve, reject) => {
      this.props.form.validateFieldsAndScroll((err, values) => {
        if (!err) {
          const { dataSource, rowColumns, validateStatus } = this.state;
          const flag = rowColumns.some(item => item.className === 'required');
          if (validateStatus[0] !== 'success' || validateStatus[1] !== 'success') {
            reject();
            return message.warning(this.$t('expense.wallet.invoiceCodeName.again')); // 请重新输入发票代码或发票号码！
          }
          if (flag && !dataSource.length) {
            reject();
            return message.warning(this.$t('expense.wallet.invoiceLine.input')); // 请输入发票行信息！
          }
          const result = this.validateTable(values, dataSource);
          if (result) {
            const { invoiceTypeId, company, close } = this.props;
            const { tenantId, setOfBooksId } = company;
            const invoiceHead = {
              ...values,
              invoiceTypeId,
              tenantId,
              setOfBooksId,
              invoiceDate: values.invoiceDate.format(),
              createdMethod: 'BY_HAND',
            };
            this.setState({ saveLoading: true });
            service
              .invoiceSave({ invoiceHead, invoiceLineList: result })
              .then(() => {
                if (isClose) {
                  message.success(this.$t('expense.wallet.invoice.success.again')); // 保存成功，可以录入新的发票！
                } else {
                  close && close(true);
                  message.success(this.$t('structure.saveSuccess')); // 保存成功
                }
                this.setState({ saveLoading: false });
                resolve();
              })
              .catch(err => {
                reject();
                message.error(err.response.data.message);
                this.setState({ saveLoading: false });
              });
          } else {
            reject();
          }
        } else {
          reject();
        }
      });
    });
  };

  // 表格所有行校验
  validateTable = (values, dataSource) => {
    let totalAmount = 0;

    let taxAmount = 0;

    const resultDate = [];
    const flag = dataSource.every(data => {
      if (this.validateRow(data)) {
        const row = { ...data };
        delete row.id;
        delete row.isEdit;
        delete row.closeAuto;
        resultDate.push(row);
        totalAmount += data.detailAmount ? Number(data.detailAmount) : 0;
        taxAmount += data.taxAmount ? Number(data.taxAmount) : 0;
        return true;
      } else {
        return false;
      }
    });
    if (flag) {
      let result1 = false;

      let result2 = false;
      if (this.isNumber(values.invoiceAmount) && values.invoiceAmount < totalAmount) {
        message.warning(this.$t('expense.wallet.invoiceAmount.warning')); // 发票行金额之和不可大于整张发票金额
      } else {
        result1 = true;
      }
      if (this.isNumber(values.taxTotalAmount) && values.taxTotalAmount < taxAmount) {
        message.warning(this.$t('expense.wallet.taxAmount.warning')); // 发票行税额之和不可大于整张发票税额
      } else {
        result2 = true;
      }
      if (result1 && result2) {
        return resultDate;
      }
      return false;
    }
    return false;
  };

  // 表格行校验
  validateRow = data => {
    const { rowColumns } = this.state;
    const notEmpty = rowColumns.every(item => {
      return item.className ? data[item.dataIndex] || String(data[item.dataIndex]) === '0' : true;
    });
    if (notEmpty) {
      const flag = data.hasOwnProperty('detailAmount') && data.hasOwnProperty('taxRate');
      if (flag) {
        const taxAmount = Number(data.taxAmount) || 0;
        const rate = data.taxRate.split('%')[0] / 100;
        const result =
          (data.detailAmount * rate).toFixed(2) >= taxAmount && data.detailAmount >= taxAmount;
        if (result) {
          return true;
        } else {
          message.warning(this.$t('expense.wallet.invoice.input.again')); // `发票行 ${data.invoiceLineNum} 行税额不得大于金额*税率，请检查并修改！`
          return false;
        }
      }
      return true;
    } else {
      message.warning(this.$t('common.notEmpty')); // 必输字段不可为空
      return false;
    }
  };

  // 底部 再录一张  重新初始化
  handleAddMore = () => {
    this.setState({ moreLoading: true });
    this.handleSubmit(true)
      .then(() => {
        this.getSelected(this.state.invoiceTypeId);
        this.setState({ moreLoading: false });
      })
      .catch(() => {
        this.setState({ moreLoading: false });
      });
  };

  // 底部 取消
  handleCancel = () => {
    this.props.close && this.props.close();
  };

  // 获取 index
  getFinalIndex = index => {
    const { current, pageSize } = this.state.pagination;
    const currentPage = current || 1;
    return (currentPage - 1) * pageSize + index;
  };

  // 表格渲染
  tableRender = (value, record, index, dataIndex, type) => {
    if (record.isEdit) {
      const onChange = this.rowTableChange(index, dataIndex, type);
      switch (type) {
        case 'input':
          return (
            <Input
              onChange={onChange}
              defaultValue={value}
              size="small"
              title={value}
              placeholder={this.$t('common.please.enter')}
            />
          );
        case 'number':
          return (
            <InputNumber
              min={0}
              onChange={onChange}
              defaultValue={value}
              size="small"
              title={value}
              placeholder={this.$t('common.please.enter')}
            />
          );
        case 'select':
          return (
            <Select
              size="small"
              onChange={onChange}
              value={value}
              dropdownMatchSelectWidth={false}
              getPopupContainer={trigger => trigger.parentNode}
              placeholder={this.$t('common.please.select')}
            >
              {this.state.taxRate.map(item => <Option key={item}>{item}</Option>)}
            </Select>
          );
        case 'auto':
          return (
            <InputNumber
              min={0}
              onChange={onChange}
              value={value}
              size="small"
              title={value}
              placeholder={this.$t('common.please.enter')}
            />
          );
      }
    } else {
      return <Popover content={value}>{value}</Popover>;
    }
  };

  // 表格中控件onChange回调
  rowTableChange = (index, dataIndex, type) => {
    const { dataSource } = this.state;
    const finalIndex = this.getFinalIndex(index);
    return e => {
      const dataSourceRecord = dataSource[finalIndex];
      dataSourceRecord[dataIndex] = type === 'input' ? e.target.value : e;
      switch (dataIndex) {
        case 'detailAmount':
        case 'taxRate':
          this.rateAutoCalculate(dataSourceRecord);
          break;
        case 'taxAmount':
          dataSourceRecord.closeAuto = true;
          break;
        case 'unitPrice':
        case 'num':
          this.totalAutoCalculate(dataSourceRecord);
          break;
      }
      this.setState({ dataSource });
    };
  };

  // 发票行税额自动计算
  rateAutoCalculate = record => {
    if (
      !record.closeAuto &&
      this.state.isShowTaxAmount &&
      record.taxRate &&
      this.isNumber(record.detailAmount)
    ) {
      const rate = record.taxRate ? record.taxRate.split('%')[0] / 100 : 0;
      const tax = record.detailAmount * rate;
      record.taxAmount = tax.toFixed(2);
    }
  };

  // 发票行金额自动计算
  totalAutoCalculate = record => {
    if (this.isNumber(record.unitPrice) && this.isNumber(record.num)) {
      record.detailAmount = (record.unitPrice * record.num).toFixed(2);
      this.rateAutoCalculate(record);
    }
  };

  // 检验是否是数字
  isNumber = value => {
    return /^[0-9]+[.]?[0-9]*$/.test(value);
  };

  // 增加一行
  addRowTable = () => {
    const { dataSource, dataCache, isShowNum } = this.state;
    const { tenantId, setOfBooksId } = this.props.company;
    let data = {
      id: new Date().getTime(),
      isEdit: 'new',
      tenantId,
      setOfBooksId,
    };
    isShowNum && (data = { ...data, ...{ num: 1 } });
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
    } else if (record.isEdit === 'new') {
      dataSource.splice(finalIndex, 1);
      dataCache.splice(finalIndex, 1);
    } else {
      dataSource[finalIndex] = dataCache[finalIndex];
    }
    this.setState({ dataSource });
  };

  // 行保存
  rowSave = (index, record) => {
    const { dataSource } = this.state;
    const finalIndex = this.getFinalIndex(index);
    if (this.validateRow(record)) {
      dataSource[finalIndex].isEdit = false;
      // dataSource[finalIndex].closeAuto = false;
      this.setState({ dataSource });
    }
  };

  // 行删除
  rowDelete = index => {
    const { dataSource, dataCache } = this.state;
    const finalIndex = this.getFinalIndex(index);
    dataSource.splice(finalIndex, 1);
    dataCache.splice(finalIndex, 1);
    this.setState({ dataSource });
    message.success(this.$t('common.delete.success'));
  };

  render() {
    const {
      invoiceHeadList,
      purchaserList,
      sellerList,
      saveLoading,
      rowColumns,
      dataSource,
      pagination,
      spinLoading,
      validateStatus,
      helpMessage,
      moreLoading,
    } = this.state;
    const { invoiceTypeId, menuList } = this.props;
    const { getFieldDecorator } = this.props.form;
    const titleLayout = {
      labelCol: { span: 12 },
      wrapperCol: { span: 12 },
    };
    const titleLayout2 = {
      labelCol: { span: 5 },
      wrapperCol: { span: 15 },
    };
    const invoiceTypeLayout = {
      labelCol: { span: 5 },
      wrapperCol: { span: 5 },
    };

    return (
      <div className="new-invoice">
        <div className="common-item-title clear">
          {this.$t('expense.wallet.invoiceHead.info') /* 发票头信息 */}
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
            <a style={{ float: 'right' }}>
              {this.$t('expense.wallet.invoice.input') /* 发票填写说明 */}
            </a>
          </Popover>
        </div>

        <Col className="invoice-col type-select">
          <FormItem
            {...invoiceTypeLayout}
            label={this.$t('expense.wallet.invoiceType') /* 发票类型 */}
            required
          >
            {getFieldDecorator('invoiceTypeId', {
              initialValue: { key: invoiceTypeId },
            })(
              <Select
                placeholder={this.$t('common.please.select')}
                onChange={this.invoiceChange}
                style={{ width: '100%' }}
                dropdownMatchSelectWidth={false}
                disabled={saveLoading}
                labelInValue
              >
                {menuList.map(item => {
                  return <Option key={item.id}>{item.invoiceTypeName}</Option>;
                })}
              </Select>
            )}
          </FormItem>
        </Col>

        <Spin size="large" spinning={spinLoading}>
          <Form onSubmit={this.onSubmit}>
            {invoiceHeadList.map(item => {
              return (
                <Col
                  span={item.colSpan || 10}
                  key={item.dataIndex}
                  className={
                    item.label === this.$t('common.remark')
                      ? 'invoice-col last-invoice-item'
                      : 'invoice-col'
                  }
                >
                  <FormItem
                    {...(item.colSpan ? titleLayout2 : titleLayout)}
                    key={item.dataIndex}
                    validateStatus={validateStatus[item.validateStatus]}
                    help={helpMessage[item.help]}
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
                          message: item.placeholder || this.$t('common.please.enter'),
                        },
                        ...(item.rules || []),
                      ],
                    })(this.itemType(item))}
                  </FormItem>
                </Col>
              );
            })}

            {!!purchaserList.length && (
              <div className="title-top clear">
                <div className="common-item-title">
                  {this.$t('expense.wallet.buy.info') /* 购方信息 */}
                </div>
              </div>
            )}
            {purchaserList.map(item => {
              return (
                <FormItem
                  {...titleLayout2}
                  label={item.label}
                  className="invoice-col"
                  key={item.dataIndex}
                >
                  {getFieldDecorator(item.dataIndex, {
                    rules: [
                      {
                        required: item.required,
                        message: this.$t('common.please.enter'),
                      },
                    ],
                  })(this.itemType(item))}
                </FormItem>
              );
            })}

            {!!sellerList.length && (
              <div className="title-top clear">
                <div className="common-item-title">
                  {this.$t('expense.wallet.sell.info') /* 销方信息 */}
                </div>
              </div>
            )}
            {sellerList.map(item => {
              return (
                <FormItem
                  {...titleLayout2}
                  label={item.label}
                  key={item.dataIndex}
                  className="invoice-col"
                >
                  {getFieldDecorator(item.dataIndex, {
                    rules: [
                      {
                        required: item.required,
                        message: this.$t('common.please.enter'),
                      },
                    ],
                  })(this.itemType(item))}
                </FormItem>
              );
            })}

            {!!rowColumns.length && (
              <div className="title-top clear">
                <div className="common-item-title">
                  {this.$t('expense.wallet.invoiceLine.info') /* 发票行信息 */}
                </div>
                <div style={{ margin: '15px 0' }}>
                  <Button onClick={this.addRowTable}>
                    <Icon type="plus" />
                    {this.$t('expense.new.invoice.line') /* 新建发票行 */}
                  </Button>
                </div>
                <Table
                  rowKey="id"
                  columns={rowColumns}
                  dataSource={dataSource}
                  pagination={pagination}
                  size="small"
                />
              </div>
            )}

            <div className="slide-footer">
              <Button type="primary" htmlType="submit" loading={saveLoading}>
                {this.$t('common.save')}
              </Button>
              <Button onClick={this.handleAddMore} loading={moreLoading}>
                {this.$t('expense.wallet.invoice.again') /* 再录一张 */}
              </Button>
              <Button onClick={this.handleCancel}>{this.$t('common.cancel')}</Button>
            </div>
          </Form>
        </Spin>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    language: state.languages,
    company: state.user.company,
  };
}

const WrappedNewInvoice = Form.create()(NewInvoice);
export default connect(mapStateToProps)(WrappedNewInvoice);

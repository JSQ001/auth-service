import React from 'react';
import { connect } from 'dva';
import baseService from 'share/base.service';
import moment from 'moment';
import SelectApplicationType from 'widget/select-application-type';
import SelectType from 'components/Widget/Template/invoice/select-type';
import AddInvoice from 'components/Widget/Template/invoice/create-invoice-modal';
import SelectInvoice from 'components/Widget/Template/invoice/select-invoice';
import AddInvoiceLine from 'components/Widget/Template/invoice/edit-table';
import InvoiceDetail from 'components/Widget/Template/invoice/invoice-detail';
import { renderFormItem } from 'utils/utils';

import {
  Alert,
  Form,
  Switch,
  Icon,
  Input,
  Select,
  Button,
  Row,
  Col,
  message,
  Card,
  Popover,
  InputNumber,
  DatePicker,
  Spin,
  Popconfirm,
  Affix,
  Badge,
} from 'antd';
const FormItem = Form.Item;
const Option = Select.Option;
const { TextArea } = Input;
import 'styles/my-account/new-expense.scss';

import InvoiceInfo from 'containers/reimburse/my-reimburse/invoice-info';
import NewShare from 'containers/reimburse/my-reimburse/new-share';
import reimburseService from 'containers/reimburse/my-reimburse/reimburse.service';
import Upload from 'widget/upload-button';
import config from 'config';
import SelectApplication from 'containers/reimburse/my-reimburse/select-application';

class NewExpense extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      businessCardConsumptions: [],
      nowBusinessCardConsumptionIndex: 0,
      expenseType: {},
      invoiceDetail: [],
      shareData: [],
      showAddInvoice: false,
      importInvoice: false,
      showInvoiceDetail: false,
      invoiceId: '',
      invoiceType: '',
      sharedAmount: 0,
      sharedObj: {},
      shareByHand: false,
      selectedInvoice: [],
      loading: false,
      saving: false,
      editTableRefresh: 0,
      nowPage: 'type', //type-费用类型选择、发票录入、商务卡消费选择页  form-费用表单页
      attachments: [],
      nowExpense: {},
      currencyList: [],
      nowCurrency: {},
      isShowInvoice: false,
      shareVisible: false,
      isCreateByApplication: false,
      currentRate: '',
      applicationData: [],
      relatedApplication: false,
      typeSource: 'expenseType', //expenseType invoice businessCard,
      saveLoading: false,
      shareModel: {},
      record: {},
      visible: false,
      headerData: {},
      editModel: {},
      defaultApportion: {},
      isCopy: false,
      againLoading: false,
      copyLoading: false,
      applicationList: [],
      attachmentOid: [],
      isRefreshShareTabel: false,
      shareParams: {},
      invoiceData: {},
      fileList: [],
      isRefresh: false,
      isCalculation: true, //是否需要计算费用金额
      shareList: [],
      showSelectApplication: false,
      selectedApplicationData: [],
      applincationParams: {},
      amount: 0,
      shareConfig: [
        {
          title: '公司',
          type: 'chooser',
          listType: 'company_share_report',
          labelKey: 'name',
          valueKey: 'id',
          dataIndex: 'company',
          visible: 'companyVisible',
          dist: 'companyDistFlag',
          defaultValue: [
            {
              id: props.params.headerData.companyId,
              name: props.params.headerData.companyName,
            },
          ],
          width: 200,
        },
        {
          title: '部门',
          type: 'chooser',
          listType: 'department_share_report',
          labelKey: 'name',
          valueKey: 'id',
          visible: 'departmentVisible',
          dist: 'departmentDistFlag',
          default: 'departmentName',
          dataIndex: 'department',
          width: 200,
          defaultValue: [
            {
              id: props.params.headerData.departmentId,
              name: props.params.headerData.departmentName,
            },
          ],
        },
        {
          title: '责任中心',
          type: 'chooser',
          listType: 'responsibility_report',
          labelKey: 'responsibilityCenterName',
          valueKey: 'id',
          visible: 'resVisible',
          dist: 'resCenterDistFlag',
          default: 'resName',
          dataIndex: 'responsibility',
          width: 200,
        },
      ],
      columns: [
        {
          title: '分摊金额',
          dataIndex: 'shareAmount',
          type: 'inputNumber',
          width: 160,
          //fixed: 'right',
          key: 'shareAmount',
        },
        {
          title: '操作',
          type: 'operation',
          dataIndex: 'operation',
          width: 200,
        },
      ],
    };
  }

  onCancel = () => {
    this.props.close(false);
  };

  getCurrencyFromList = currencyCode => {
    let result = false;
    this.state.currencyList.map(item => {
      if (item.currency === currencyCode) {
        result = item;
      }
    });
    return result;
  };

  componentDidMount() {
    baseService.getCurrencyList(this.props.company.baseCurrency).then(res => {
      this.setState({ currencyList: res.data });
    });
    let { record, isCopy } = this.props.params;
    if (record.id) {
      let fileList = isCopy
        ? []
        : record.attachments.map(item => {
            return {
              ...item,
              uid: item.attachmentOid,
              name: item.fileName,
              status: 'done',
            };
          });
      reimburseService.getExpenseDetail(record.id).then(res => {
        let invoiceDetail = [];
        if (!isCopy) {
          res.data.invoiceHeads.map(item => {
            item.invoiceLineList.map(i => {
              invoiceDetail.push({
                ...i,
                invoice: { ...item },
              });
            });
          });
        }
        this.setState(
          {
            attachmentOid: isCopy ? [] : record.attachmentOidList,
            record,
            isCopy,
            amount: record.amount,
            taxAmount: record.taxAmount,
            relatedApplication: res.data.applicationModel !== 'NO_NEED',
            expenseType: {
              ...record,
              fields: res.data.fields,
              id: res.data.expenseTypeId,
              name: res.data.expenseTypeName,
            },
            fileList,
            invoiceDetail,
          },
          this.checkShareDetail
        );
      });
      !isCopy && this.getShareDetail();
    }
    this.getTypeConfig();
  }

  getTypeConfig = () => {
    let { headerData, record } = this.props.params;
    let { columns, shareConfig } = this.state;
    let params = {
      expenseReportTypeId: headerData.documentTypeId,
      headerId: headerData.id,
    };

    if (headerData.expTaxDist === 'TAX_IN') {
      //含税
      let temp = [
        {
          title: '不含税金额',
          dataIndex: 'noTaxAmount',
          type: 'text',
          width: 160,
          key: 'noTaxAmount',
        },
        {
          title: '分摊税额',
          dataIndex: 'sharedTaxAmount',
          type: 'text',
          width: 160,
          key: 'sharedTaxAmount',
        },
      ];
      temp.reverse().map(item => {
        columns.splice(1, 0, item);
      });
    }
    reimburseService.getConfigDetail(params).then(res => {
      let reTypeDetail = res.data;
      let distSetting = res.data.expenseReportTypeDistSetting;
      let arr = reTypeDetail.expenseDimensions || [];
      arr.reverse().map(item => {
        columns.splice(0, 0, {
          title: item.name,
          type: 'select',
          labelKey: 'dimensionItemName',
          valueKey: 'id',
          dataIndex: item.dimensionField,
          width: 120,
          getOptions: () => this.getDimValue(item.dimensionId, item.dimensionField),
        });
      });
      //公司部门，责任中心分摊规则
      //不参与分摊或参与分摊但隐藏时不显示  dist: departmentDistFlag
      console.log(distSetting);
      let config = shareConfig.filter(item =>
        ['EDITABLE', 'READ_ONLY'].includes(distSetting[item.visible])
      );

      config.reverse().map(item => {
        //只读时，设置显示的默认值
        //item.visible = distSetting[item.visible]
        if (distSetting[item.visible] === 'READ_ONLY') {
          item.disabled = true;
        }
        item.listExtraParams = {
          expenseTypeId: headerData.documentTypeId,
        };
        columns.splice(0, 0, { ...item });
      });
      this.setState(
        {
          reTypeDetail,
          columns,
        },
        () => {
          //编辑时，获取维度可选维值
          if (record.id) {
            arr.map(item => this.getDimValue(item.dimensionId, item.dimensionField));
          }
        }
      );
    });
  };

  //获取维值
  getDimValue = (id, dataIndex) => {
    const { columns, dimParams } = this.state;
    let { headerData } = this.props.params;

    let params = {
      dimensionId: id,
      enabled: true,
      companyId: headerData.companyId,
      userId: headerData.applicantId,
      unitId: headerData.departmentId,
      ...dimParams,
    };
    reimburseService.getDimValueById(params).then(res => {
      columns.map(item => {
        if (item.dataIndex === dataIndex) {
          item.options = res.data;
        }
      });
      this.setState({
        columns,
        editTableRefresh: new Date().getTime(),
      });
    });
  };

  //获取分摊详情
  getShareDetail = () => {
    const { page, size } = this.editTable.state.pagination;
    let { headerData } = this.props.params;
    let params = {
      lineId: this.props.params.record.id,
      page: page,
      size: size,
    };
    reimburseService.getShareDetail(params).then(res => {
      let sharedAmount = 0;
      let shareData = res.data.map(item => {
        sharedAmount += item.amount;
        let data = {
          ...item,
          company: [
            {
              id: item.companyId,
              name: item.companyName,
            },
          ],
          department: [
            {
              departmentId: item.departmentId,
              name: item.departmentName,
            },
          ],
        };
        if (headerData.expTaxDist === 'TAX_IN') {
          data.shareAmount = data.amount;
          data.noTaxAmount = data.noTaxDistAmount;
          data.sharedTaxAmount = data.taxDistAmount;
        }
        return data;
      });
      this.setState({
        sharedAmount,
        shareData,
      });
    });
  };

  //复制操作设置值
  setCopyEvent = () => {
    this.setState(
      {
        fileList: [],
        attachmentOid: [],
        invoiceDetail: [],
        shareData: [],
      },
      () => {
        this.upload && this.upload.reset();
      }
    );
  };

  //获取默认分摊行
  setDefaultApplication = () => {
    let applicationData = [];
    let defaultApportion = this.state.defaultApportion;

    let obj = {
      company: {
        id: defaultApportion.companyId,
        name: defaultApportion.companyName,
      },
      department: {
        departmentId: defaultApportion.departmentId,
        name: defaultApportion.departmentName,
      },
    };
    defaultApportion.costCenterItems &&
      defaultApportion.costCenterItems.map(o => {
        obj[o.costCenterOid] = {
          key: o.costCenterItemId,
          label: o.costCenterItemName,
        };
      });
    obj.defaultApportion = true;
    obj.rowKey = 1;
    obj.isEdit = false;
    applicationData.push(obj);
    this.setState({ applicationData, isRefreshShareTabel: !this.state.isRefreshShareTabel });
  };

  //再记一笔
  againSave = e => {
    this.setState({ againLoading: true });
    this.getFormValues(() => {
      message.success('保存成功！');
      this.setState({
        againLoading: false,
        fileList: [],
        attachmentOid: [],
        invoiceDetail: [],
        shareData: [],
        isCopy: true,
      });
      let value = this.props.form.getFieldsValue();
      for (let name in value) {
        value[name] = null;
      }
      value.expenseType = {};
      this.props.form.setFieldsValue(value);

      if (this.state.headerData.relatedApplication === false) {
        this.setDefaultApplication();
      }
      this.props.params.refresh && this.props.params.refresh(true);
    }, this.state.isCopy);
  };

  //复制
  copy = e => {
    this.setState({ copyLoading: true });
    this.getFormValues(() => {
      message.success('保存成功！');
      this.props.params.refresh && this.props.params.refresh(true);
      this.setState(
        {
          copyLoading: false,
          isCopy: true,
        },
        () => {
          this.setCopyEvent();
        }
      );
    }, this.state.isCopy);
  };

  //提交
  handleSave = e => {
    e.preventDefault();
    this.setState({ saveLoading: true });
    this.getFormValues(() => {
      message.success('保存成功！');
      this.props.params.refresh && this.props.params.refresh(true);
      this.props.close(true);
      this.setState({ saveLoading: false });
    }, this.state.isCopy);
  };

  getFormValues = (callback, flag) => {
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (err) {
        this.setState({ saveLoading: false, copyLoading: false, againLoading: false });
        return;
      }
      const {
        expenseType,
        invoiceDetail,
        record,
        attachmentOid,
        isCopy,
        taxAmount,
        amount,
      } = this.state;
      const { headerData } = this.props.params;
      const { dataSource } = this.editTable.state;
      let sharedAmount = 0;
      dataSource.map(item => (sharedAmount += Number(item.shareAmount)));
      if (amount < sharedAmount) {
        message.warning('分摊金额超出报账金额！');
        this.setState({ saveLoading: false, copyLoading: false, againLoading: false });
        return;
      }

      let invoiceHeads = [];
      let head = {};
      invoiceDetail.map(item => {
        let line = { ...item };
        delete line.invoice;
        if (!head[item.invoice.id]) {
          head[item.invoice.id] = { ...item.invoice };
          head[item.invoice.id].invoiceLineList = [line];
        } else {
          head[item.invoice.id].push(line);
        }
      });
      for (let name in head) {
        head[name].totalAmount = head[name].totalAmount || 0;
        invoiceHeads.push(head[name]);
      }
      values.expenseTypeId = values.expenseType.id;
      values.expReportHeaderId = this.props.params.headerData.id;
      values.taxAmount = taxAmount;
      values.expenseReportDistList = [];
      values.installmentDeductionFlag = 'N';
      values.invoiceHeads = invoiceHeads;
      !flag && (values.id = record.id);
      values.attachmentOid = (attachmentOid || []).toString();
      values.fields =
        expenseType.fields &&
        expenseType.fields.map(item => {
          let temp = {
            ...item,
            value: values[item.id],
          };
          //chooser类型
          if (['PARTICIPANTS', 'PARTICIPANT'].includes(item.fieldType)) {
            temp.value = temp.value[0].id;
          }
          return temp;
        });
      values.expenseReportDistList = this.editTable.state.dataSource.map(item => {
        return {
          ...item, //taxDistAmount税额， noTaxDistAmount不含税额
          taxDistAmount:
            headerData.expTaxDist === 'TAX_IN'
              ? item.sharedTaxAmount
              : (taxAmount * item.shareAmount) / values.expenseAmount,
          amount:
            headerData.expTaxDist === 'TAX_IN'
              ? item.shareAmount
              : item.shareAmount + (taxAmount * item.shareAmount) / values.expenseAmount,
          noTaxDistAmount: headerData.expTaxDist === 'TAX_IN' ? item.noTaxAmount : item.shareAmount,
          departmentId:
            item.department && (item.department[0].departmentId || item.department[0].id),
          companyId: item.company && item.company[0].id,
        };
      });

      if (values.id) {
        for (let name in record) {
          !values[name] && (values[name] = record[name]);
        }
      }

      reimburseService
        .newReportLine(values)
        .then(res => {
          callback();
        })
        .catch(err => {
          message.error('保存失败：' + err.response && err.response.data.message);
          this.setState({ saveLoading: false, copyLoading: false, againLoading: false });
        });
    });
  };

  //显示分摊规则
  checkShareDetail = () => {
    const { reTypeDetail, record, expenseType } = this.state;
    const { headerData } = this.props.params;
    let shareByHand = false;
    let relatedApplication = false;
    switch (expenseType.applicationModel) {
      case 'EITHER':
        {
          shareByHand = true;
          relatedApplication = true;
        }
        break;
      case 'MUST':
        {
          let sign;
          let data = this.props.form.getFieldsValue();
          let amount = headerData.expTaxDist === 'TAX_IN' ? data.amount : data.expenseAmount;
          if (expenseType.contrastSign === '01') {
            sign = expenseType[record.id ? 'contrastAmount' : 'amount'] > amount;
          } else {
            sign = expenseType[record.id ? 'contrastAmount' : 'amount'] >= amount;
          }
          shareByHand = !sign;
          relatedApplication = true;
        }
        break;
      case 'NO_NEED':
        {
          shareByHand = true;
          relatedApplication = false;
        }
        break;
    }
    this.setState({
      expenseType,
      relatedApplication,
      shareByHand,
    });
  };

  //选择费用类型，所选类型是否有动态字段
  handleSelectExpenseType = expenseType => {
    this.setState({ expenseType }, this.checkShareDetail);
  };

  //录入发票事件
  isInputInvoiceChange = value => {
    if (!value) {
      this.refs.invoice.resetFields();
    }
    this.props.form.setFieldsValue({ actualAmount: '', amount: '' });
    this.setState({ isShowInvoice: value, currentRate: '' }, () => {
      if (this.state.headerData.relatedApplication === false) {
        this.setDefaultApplication();
      }
    });
  };

  //新建分摊按钮事件
  // newShare = (value) => {
  //     this.setState({ shareVisible: true, isCreateByApplication: value, shareModel: {} });
  // }

  //编辑分摊
  editShare = index => {
    this.setState({
      shareVisible: true,
      isCreateByApplication: this.state.applicationData[index].isCreateByApplication,
      shareModel: this.state.applicationData[index],
    });
  };

  //设置默认分摊行金额
  setDefaultAmount = (value, flag) => {
    //关联申请单不需要计算默认分摊行金额
    if (this.state.headerData.relatedApplication === true) return;

    if (!flag) {
      value = this.props.form.getFieldValue('actualAmount');
    }

    let applicationData = this.state.applicationData;

    if (applicationData && applicationData.length) {
      let amount = 0;

      applicationData.map(o => {
        if (!o.defaultApportion) {
          amount += parseFloat(o.cost);
        }
      });

      let temp = applicationData[0];

      value = parseFloat(value);

      if (value || value === 0) {
        temp.cost = value - amount;
        temp.cost = this.toDecimal2(temp.cost);
      } else {
        temp.cost = '';
      }

      this.setState({ applicationData, isRefreshShareTabel: !this.state.isRefreshShareTabel });
    }
  };

  //从申请单新建分摊
  newShareByApplication = () => {
    let costType = this.props.form.getFieldValue('costType');
    if (!costType || !costType.length) {
      message.warning('请先选择费用类型！');
      return;
    }
    this.setState({
      showSelectApplication: true,
      applincationParams: this.props.params.headerData,
      selectedData: [],
    });
  };

  amountChange = value => {
    this.setDefaultAmount(value, true);
  };

  //报账金额改变，计算费用金额
  reimburseAmountChange = value => {
    const { selectedInvoice } = this.state;
    let taxAmount = 0;
    selectedInvoice.map(item => {
      //税额为发票头deductionFlag===y的行税额总计
      if (item.invoice.deductionFlag === 'Y') taxAmount += item.taxAmount;
    });
    let expenseAmount = value > taxAmount ? value - taxAmount : value;
    this.setState(
      {
        taxAmount,
        amount: value,
      },
      () => {
        this.props.form.setFieldsValue({ expenseAmount: expenseAmount });
        this.checkShareDetail();
      }
    );
  };

  // 公司部门修改，重置维度
  resetDimValue = index => {
    let { reTypeDetail } = this.state;
    let { dataSource } = this.editTable.state;
    let arr = reTypeDetail.expenseDimensions || [];
    arr.map(item => {
      dataSource[index][item.dimensionField] = '';
    });
    console.log(dataSource);
    this.setState({
      dataSource,
      editTableRefresh: new Date().getTime(),
    });
  };

  handleLineEvent = (key, value, index) => {
    let { taxAmount, amount, columns } = this.state;
    let { headerData } = this.props.params;
    let { dataSource } = this.editTable.state;
    if (headerData.expTaxDist === 'TAX_IN') {
      switch (key) {
        //taxAmount税额 ， sharedAmount:分摊金额(分摊行输入的金额总计)  amount: 报账金额，
        case 'shareAmount':
          {
            let sharedTaxAmount = ((value * taxAmount) / amount).toFixed(2);
            dataSource[index].noTaxAmount = value - sharedTaxAmount;
            dataSource[index].sharedTaxAmount = sharedTaxAmount;

            console.log(dataSource[index]);
            if (
              dataSource[index].usableAmount &&
              Number(dataSource[index].usableAmount) < Number(value)
            ) {
              message.warning('分摊金额大于关联申请单可用金额！');
              dataSource[index].shareAmount = dataSource[index].usableAmount;
              columns.map(item => {
                if (item.dataIndex === key) {
                  item.override = {
                    max: Number(dataSource[index].usableAmount),
                  };
                }
              });
            }
            this.setState(
              {
                editTableRefresh: new Date().getTime(),
              },
              this.setSharedAmount
            );
          }
          break;

        case 'company':
          {
            this.resetDimValue(index);
            if (value && value.length) {
              columns.map(item => {
                if (item.dataIndex === 'responsibility') {
                  item.listExtraParams = {
                    ...item.listExtraParams,
                    companyId: value[0].id,
                  };
                }
              });
              this.setState({
                dimParams: {
                  ...this.state.dimParams,
                  companyId: value[0].id,
                },
              });
            }
          }
          break;
        case 'department':
          {
            this.resetDimValue(index);
            if (value && value.length) {
              columns.map(item => {
                if (item.dataIndex === 'responsibility') {
                  item.listExtraParams = {
                    ...item.listExtraParams,
                    departmentId: value[0].id,
                  };
                }
              });
              this.setState({
                dimParams: {
                  ...this.state.dimParams,
                  unitId: value[0].id,
                },
              });
            }
          }
          break;
        case 'responsibility':
          {
          }
          break;
      }
    }
  };

  //发票价税合计改变
  invoiceAmountChange = (value, rate, isCalculation) => {
    this.setState({ isCalculation: isCalculation });

    if (!isCalculation || (!rate && rate !== 0)) {
      this.props.form.setFieldsValue({ amount: value });
      this.props.form.setFieldsValue({ actualAmount: value });
      this.setDefaultAmount(value, true);
      return;
    }
    this.props.form.setFieldsValue({ amount: value });

    let result = '';

    if (value && (rate || rate === 0)) {
      result = value / (1 + rate);
      result = this.toDecimal2(result);
    }

    this.props.form.setFieldsValue({ actualAmount: result });

    this.setDefaultAmount(result, true);
  };

  //税率改变
  invoiceRateChange = (rate, isCalculation) => {
    this.setState({ currentRate: rate, isCalculation: isCalculation });
    this.props.form.setFieldsValue({ amount: '', actualAmount: '' });
    this.setDefaultAmount('', true);
  };

  //四舍五入 保留两位小数
  toDecimal2 = x => {
    var f = parseFloat(x);
    if (isNaN(f)) {
      return false;
    }
    var f = Math.round(x * 100) / 100;
    var s = f.toString();
    var rs = s.indexOf('.');
    if (rs < 0) {
      rs = s.length;
      s += '.';
    }
    while (s.length <= rs + 2) {
      s += '0';
    }
    return s;
  };

  //上传附件
  handleUpload = values => {
    this.setState({ attachmentOid: values });
  };

  //检查金额
  checkPrice = (rule, value, callback) => {
    if (value > 0) {
      callback();
      return;
    }
    callback('金额不能小于等于0！');
  };

  //切换页面
  changeView = () => {
    this.setState({ nowPage: 'type', typeSource: 'expenseType' }, () => {
      this.resetForm(true);
      this.setDefaultApplication();
    });
  };

  //获取分摊列表
  getShareData = (applicationData, flag) => {
    this.setState({ applicationData }, () => {
      flag && this.setDefaultAmount();
    });
  };

  //分摊金额改变
  costChange = applicationData => {
    this.setState({ applicationData }, () => {
      this.setDefaultAmount();
    });
  };

  //设置已分摊金额
  setSharedAmount = () => {
    const { dataSource } = this.editTable.state;
    let sharedAmount = 0;
    dataSource.map(item => (sharedAmount += Number(item.shareAmount)));
    this.setState({ sharedAmount });
  };

  //选择申请单的回调
  handleListOk = value => {
    let { shareData, taxAmount, amount, sharedAmount } = this.state;
    let { headerData } = this.props.params;
    value = value.map(item => {
      let sharedTaxAmount = ((item.usableAmount * taxAmount) / amount).toFixed(2);
      let linkData = {
        ...item,
        sourceDocumentCategory: 'EXP_REQUISITION',
        sourceDocumentId: item.requestId,
        sourceDocumentDistId: item.id,
        company: [{ id: item.companyId, name: item.companyName }],
        department: [{ departmentId: item.departmentId, name: item.departmentName }],
        shareAmount: item.usableAmount,
        noTaxAmount: item.usableAmount - sharedTaxAmount,
        sharedTaxAmount: sharedTaxAmount,
      };
      if (headerData.expTaxDist === 'TAX_IN') {
        sharedAmount += item.usableAmount;
        let sharedTaxAmount = ((item.usableAmount * taxAmount) / amount).toFixed(2);
        linkData.noTaxAmount = item.usableAmount - sharedTaxAmount;
        linkData.sharedTaxAmount = sharedTaxAmount;
      }
      delete linkData.id;
      return linkData;
    });
    this.setState({
      showSelectApplication: false,
      selectedApplicationData: value,
      sharedAmount,
      shareData: this.myConcat(shareData.concat(value)),
    });
  };

  handleInvoice = value => {
    this.setState({
      invoiceType: value,
      showAddInvoice: true,
    });
  };

  handleCloseInvoice = () => {
    this.setState({ showAddInvoice: false });
  };

  handleImportInvoice = () => {
    this.setState({
      importInvoice: true,
    });
  };

  handleDeleteLink = data => {
    const { record } = this.props.params;
    if (record.id) {
      let params = {
        lineId: record.id,
        invoiceLineId: data.id,
      };
      reimburseService.deleteLinkInvoice(params).then(res => {
        message.success(this.$t('common.delete.success'));
      });
    }
    this.setState(
      {
        invoiceDetail: this.state.invoiceDetail.filter(
          item => data.id.toString() !== item.id.toString()
        ),
      },
      () => {
        this.setAmount(this.checkShareDetail, true);
      }
    );
  };

  //数组去重
  myConcat = (origin, key = 'id') => {
    let temp = {};
    let target = [];
    origin.map(item => (temp[item[key]] = item));
    for (let item in temp) {
      target.push(temp[item]);
    }
    return target;
  };

  //导入发票回调
  handleImportOk = result => {
    let { invoiceDetail } = this.state;
    this.setState(
      {
        invoiceDetail: this.myConcat(invoiceDetail.concat(result)),
        selectedInvoice: result,
        importInvoice: false,
        showInvoice: result.length ? true : false,
      },
      () => {
        this.setAmount(this.checkShareDetail);
      }
    );
  };

  setAmount = (callback, isDelete) => {
    const { invoiceDetail } = this.state;
    let taxAmount = 0;
    let amount = 0;
    invoiceDetail.map(item => {
      amount += item.detailAmount + item.taxAmount;
      if (item.invoice.deductionFlag === 'Y') taxAmount += item.taxAmount;
    });
    this.setState(
      {
        amount,
        taxAmount,
      },
      () => {
        let value = {
          amount: amount,
          expenseAmount: amount - taxAmount,
        };
        if (invoiceDetail.length) {
          value.price = amount;
          value.quantity = 1;
        }
        if (isDelete && !invoiceDetail.length) {
          value.price = 0;
          value.quantity = 1;
        }
        this.props.form.setFieldsValue(value);
        callback();
      }
    );
  };

  //费用体系动态字段
  renderDynamic() {
    const { getFieldDecorator } = this.props.form;
    const { record, expenseType } = this.state;
    const formItemLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 17 },
    };
    let dynamic = [].concat(record.field || []).concat(expenseType.fields || []);
    let col = [];
    let row = [];

    dynamic &&
      dynamic.map((item, index) => {
        let override = {};
        //设置chooser参数
        if (item.fieldType === 'PARTICIPANT' || item.fieldType === 'PARTICIPANTS') {
          override.listExtraParams = {
            companyId: this.props.company.id,
          };
        }
        const isOdd = (index + 1) % 2 === 0;
        col.push(
          <Col key={item.id} span={isOdd ? 11 : 12} offset={isOdd ? 1 : 0}>
            <FormItem {...formItemLayout} key={item.id} label={item.name}>
              {getFieldDecorator(item.id, {
                rules: [
                  {
                    required: true,
                    message: this.$t('common.please.select'),
                  },
                ],
                initialValue: record.id ? item.value : item.defaultValueMode,
              })(renderFormItem({ type: item.fieldType }, override))}
            </FormItem>
          </Col>
        );
      });
    for (let i = 0, j = 2; i < col.length; i = i + 2, j = i + 2) {
      row.push(col.slice(i, j));
    }
    return row.map(item => <Row key={new Date().getTime()}>{item}</Row>);
  }

  //手动添加发票
  handleSuccess = value => {
    let invoiceDetail = [];
    value.map(i => {
      let Head = i.invoiceHead;
      let Line = i.invoiceLineList;
      let invoices = {
        ...Head,
        invoiceLineList: Line,
      };
      i.invoiceLineList.map(item => {
        invoiceDetail.push({
          ...item,
          invoice: invoices,
          fromBook: true,
        });
      });
    });
    invoiceDetail = this.state.invoiceDetail.concat(invoiceDetail);
    this.setState(
      {
        invoiceDetail,
        showAddInvoice: false,
      },
      () => {
        this.setAmount(this.checkShareDetail);
      }
    );
  };

  //行保存，计算已分摊金额
  handleLineSave = data => {
    /*let { sharedAmount} = this.state
    data.map(item=>{
      sharedAmount+=item.amount||0
    })
    this.setState({
      sharedAmount
    })*/
  };

  //关联申请
  handleRelateRequest = () => {
    if (this.handleCheck()) {
      this.setState({
        showSelectApplication: true,
      });
    }
  };

  //校验是否能添加分摊行
  handleCheck = () => {
    if (!this.props.form.getFieldValue('amount')) {
      message.warning('请先输入报账金额！');
      return false;
    }
    return true;
  };

  handlePriceChange = value => {
    let data = this.props.form.getFieldsValue();
    if (data.quantity) {
      this.checkShareDetail();
      this.props.form.setFieldsValue({
        amount: value * data.price,
        expenseAmount: value * data.quantity,
      });
    }
  };

  //数量修改，计算单价
  handleQuantityChange = value => {
    const { invoiceDetail } = this.state;
    let data = this.props.form.getFieldsValue();
    if (invoiceDetail.length) {
      let amount = data.amount;
      this.props.form.setFieldsValue({ price: (amount / value).toFixed(2) });
    } else {
      this.checkShareDetail();
      data.price &&
        this.props.form.setFieldsValue({
          amount: value * data.price,
          expenseAmount: value * data.price,
        });
    }
  };

  render() {
    const { getFieldDecorator, getFieldValue } = this.props.form;
    let { headerData } = this.props.params;
    const {
      expenseType,
      loading,
      attachmentOid,
      saveLoading,
      showSelectApplication,
      selectedApplicationData,
      editModel,
      fileList,
      invoiceData,
      applicationData,
      showAddInvoice,
      importInvoice,
      selectedInvoice,
      invoiceId,
      invoiceType,
      record,
      shareData,
      columns,
      editTableRefresh,
      relatedApplication,
      shareByHand,
      invoiceDetail,
      sharedAmount,
    } = this.state;

    const formItemLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 17 },
    };

    return (
      <div style={{ padding: 20 }}>
        {this.props.params.visible && (
          <Form onSubmit={this.handleSave}>
            <Spin spinning={loading}>
              <div style={{ paddingBottom: 50 }}>
                <Row>
                  <Col span={12}>
                    <FormItem {...formItemLayout} label="费用类型">
                      {getFieldDecorator('expenseType', {
                        initialValue: { name: record.expenseTypeName, id: record.expenseTypeId },
                        rules: [{ required: true, message: '请选择' }],
                      })(
                        <SelectApplicationType
                          title="费用类型"
                          onChange={this.handleSelectExpenseType}
                          url={`${config.expenseUrl}/api/expense/report/type/section/expense/type`}
                          params={{
                            expenseReportTypeId: headerData.documentTypeId,
                            companyId: headerData.companyId,
                            departmentId: headerData.departmentId,
                            employeeId: headerData.applicantId,
                          }}
                          filter={item => item.enabled}
                        />
                      )}
                    </FormItem>
                  </Col>
                  <Col span={11} offset={1}>
                    <FormItem {...formItemLayout} label="发生日期">
                      {getFieldDecorator('expenseDate', {
                        initialValue: record.expenseDate ? moment(record.expenseDate) : null,
                        rules: [{ message: '请输入', required: true }],
                      })(<DatePicker style={{ width: '100%' }} format="YYYY-MM-DD" />)}
                    </FormItem>
                  </Col>
                </Row>
                {this.renderDynamic()}
                <FormItem
                  labelCol={{ span: 3 }}
                  label={this.$t('expense.enter.invoice') /*录入发票*/}
                >
                  {getFieldDecorator('invoice')(
                    <span>
                      <SelectType onOk={this.handleInvoice} />
                      <Button onClick={this.handleImportInvoice} style={{ marginLeft: 20 }}>
                        从票夹导入
                      </Button>
                    </span>
                  )}
                </FormItem>
                {invoiceDetail.length > 0 ? (
                  <InvoiceDetail deleteLink={this.handleDeleteLink} invoiceDetail={invoiceDetail} />
                ) : null}
                <Row>
                  <Col span={24}>
                    {this.state.isShowInvoice && (
                      <InvoiceInfo
                        onAmountChange={this.invoiceAmountChange}
                        onRateChange={this.invoiceRateChange}
                        headerData={this.state.headerData}
                        params={invoiceData}
                        ref="invoice"
                      />
                    )}
                  </Col>
                </Row>
                {expenseType.entryMode && (
                  <Row>
                    <Col span={12}>
                      {
                        <FormItem {...formItemLayout} label="单价">
                          {getFieldDecorator('price', {
                            initialValue: record.price,
                            rules: [{ required: true, validator: this.checkPrice }],
                          })(
                            <InputNumber
                              step={0.01}
                              disabled={!!invoiceDetail.length}
                              onChange={this.handlePriceChange}
                              style={{ width: '100%' }}
                              percision={2}
                            />
                          )}
                        </FormItem>
                      }
                    </Col>
                    <Col span={11} offset={1}>
                      {
                        <FormItem {...formItemLayout} label="数量">
                          {getFieldDecorator('quantity', {
                            initialValue: record.quantity,
                          })(
                            <InputNumber
                              //disabled={!!invoiceDetail.length}
                              onChange={this.handleQuantityChange}
                              step={1}
                              style={{ width: '100%' }}
                              percision={0}
                            />
                          )}
                        </FormItem>
                      }
                    </Col>
                  </Row>
                )}
                <Row>
                  <Col span={12}>
                    {
                      <FormItem {...formItemLayout} label="报账金额">
                        {getFieldDecorator('amount', {
                          initialValue: record.amount,
                          rules: [{ required: true, validator: this.checkPrice }],
                        })(
                          <InputNumber
                            step={0.01}
                            disabled={expenseType.entryMode || !!invoiceDetail.length}
                            onChange={this.reimburseAmountChange}
                            style={{ width: '100%' }}
                            percision={2}
                          />
                        )}
                      </FormItem>
                    }
                  </Col>
                  <Col span={11} offset={1}>
                    {
                      <FormItem {...formItemLayout} label="费用金额">
                        {getFieldDecorator('expenseAmount', {
                          initialValue: record.id ? record.expenseAmount : '',
                        })(
                          <InputNumber
                            disabled
                            onChange={this.amountChange}
                            step={0.01}
                            style={{ width: '100%' }}
                            percision={2}
                          />
                        )}
                      </FormItem>
                    }
                  </Col>
                </Row>
                {this.props.params.visible && (
                  <FormItem label="附件" labelCol={{ span: 3 }} wrapperCol={{ span: 20 }}>
                    {getFieldDecorator('attachmentOid')(
                      <Upload
                        wrappedComponentRef={upload => (this.upload = upload)}
                        attachmentType="BUDGET_JOURNAL"
                        uploadUrl={`${config.baseUrl}/api/upload/static/attachment`}
                        fileNum={9}
                        uploadHandle={this.handleUpload}
                        defaultFileList={fileList}
                        defaultOids={attachmentOid}
                      />
                    )}
                  </FormItem>
                )}
                <FormItem
                  label={this.$t('common.comment')}
                  labelCol={{ span: 3 }}
                  wrapperCol={{ span: 20 }}
                >
                  {getFieldDecorator('description', {
                    initialValue: record.description,
                  })(<TextArea rows={4} style={{ width: '100%' }} />)}
                </FormItem>

                <AddInvoiceLine
                  ref={ref => (this.editTable = ref && ref.wrappedInstance)}
                  columns={columns}
                  refresh={editTableRefresh}
                  lineSave={this.handleLineSave}
                  dataSource={shareData}
                  checkMethod={this.handleCheck}
                  handleEvent={this.handleLineEvent}
                  lineEvent={this.setSharedAmount}
                  onCancel={this.handleCloseInvoice}
                >
                  <FormItem labelCol={{ span: 3 }} wrapperCol={{ span: 20 }} label="分摊费用">
                    <div>
                      {relatedApplication && (
                        <Button
                          onClick={this.handleRelateRequest}
                          icon="plus"
                          style={{ marginRight: 10 }}
                        >
                          从申请单新建分摊
                        </Button>
                      )}
                      {shareByHand && (
                        <Button
                          onClick={() => {
                            this.editTable && this.editTable.addRowTable();
                          }}
                          icon="plus"
                        >
                          新建分摊
                        </Button>
                      )}
                      <div>
                        <span>分摊总金额:</span>
                        <span style={{ margin: '0 20px 0 10px' }}>
                          {this.filterMoney(this.props.form.getFieldValue('amount') || 0)}
                        </span>
                        <span>已分摊金额:</span>
                        <span style={{ marginLeft: 10 }}>{this.filterMoney(sharedAmount)}</span>
                      </div>
                    </div>
                  </FormItem>
                </AddInvoiceLine>
              </div>
              <Affix
                style={{
                  textAlign: 'center',
                  position: 'fixed',
                  bottom: 0,
                  marginLeft: '-35px',
                  width: '100%',
                  height: '50px',
                  boxShadow: '0px -5px 5px rgba(0, 0, 0, 0.067)',
                  background: '#fff',
                  lineHeight: '50px',
                  zIndex: 5,
                }}
              >
                <Button type="primary" htmlType="submit" loading={saveLoading}>
                  保存
                </Button>
                <Button
                  onClick={this.againSave}
                  loading={this.state.againLoading}
                  style={{ margin: '0 10px' }}
                >
                  再记一笔
                </Button>
                <Button
                  onClick={this.copy}
                  loading={this.state.copyLoading}
                  style={{ margin: '0 10px', marginLeft: 0 }}
                >
                  复制
                </Button>
                <Button onClick={this.onCancel}>取消</Button>
              </Affix>
            </Spin>
          </Form>
        )}
        <SelectApplication
          visible={showSelectApplication}
          onCancel={() => {
            this.setState({ showSelectApplication: false, applicationParams: {} });
          }}
          onOk={this.handleListOk}
          params={{
            applicationParams: {
              expenseTypeId: expenseType.id,
              currencyCode: headerData.currencyCode,
              expReportHeaderId: headerData.id,
            },
            type: expenseType.id,
          }}
          selectedData={selectedApplicationData}
        />
        <AddInvoice
          visible={showAddInvoice}
          params={{
            record: {},
            currency: headerData.currencyCode,
            invoiceTypeId: invoiceType.id,
          }}
          saveSuccess={this.handleSuccess}
          onCancel={this.handleCloseInvoice}
        />
        <SelectInvoice
          visible={importInvoice}
          onOk={this.handleImportOk}
          onCancel={() => {
            this.setState({ importInvoice: false });
          }}
          selectedData={selectedInvoice}
          params={{ createdBy: this.props.user.id, roleType: 'TENANT' }}
        />
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    company: state.user.company,
    companyConfiguration: state.user.companyConfiguration,
    profile: state.user.profile,
    user: state.user.currentUser,
  };
}

const WrappedNewExpense = Form.create()(NewExpense);

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(WrappedNewExpense);

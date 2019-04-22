import React from 'react';
import { connect } from 'dva';
import {
  Button,
  Form,
  Switch,
  Input,
  Checkbox,
  Radio,
  Affix,
  Row,
  Col,
  message,
  Spin,
  Select,
} from 'antd';

const CheckboxGroup = Checkbox.Group;
const FormItem = Form.Item;
import LanguageInput from 'widget/Template/language-input/language-input';
// import ExpenseAllocation from 'containers/setting/form/form-detail/expense-allocation/expense-allocation'
import formService from 'containers/setting/form/form.service';
import PropTypes from 'prop-types';
import { routerRedux } from 'dva/router';

class FormDetailBase extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      documentType: [],
      form: {
        formName: '',
        remark: '',
        formNameI18n: null,
        remarkI18n: null,
      },
      associateExpenseReport: {
        fetched: false,
        data: {},
      },
      //可关联的申请单范围
      //1001费用申请单
      //1002差旅申请单
      //1003费用申请单或者差旅申请单
      //配置都是字符串类型
      applicationOption: [
        {
          label: this.$t('form.setting.related.expense.form'), //费用申请单
          value: '1001',
        },
        {
          label: this.$t('form.setting.related.travel.form'), //差旅申请单
          value: '1002',
        },
      ],
      applicationTypeCheckedList: ['1001', '1002'],
      outApplicationAmount: true, //借款金额小于等于申请金额
      isReference: false, //关联申请单是否必选
      isHasLoanRelated: false, //是否开启借款单关联申请单的配置
      isExistLoanRelated: false, //是否有借款单关联申请单的配置
      saving: false,
    };
  }

  componentWillMount() {
    const { form, userScope, expenseTypeScope } = this.context;
    if (form) {
      if (form.associateExpenseReport) {
        formService.getFormDetail(form.referenceOid).then(res => {
          console.log(res, 'res');
          this.setState({
            associateExpenseReport: {
              fetched: true,
              data: res.data,
            },
          });
        });
      }
      let formNameI18n = [],
        remarkI18n = [];
      form.customFormResponseI18nDTOS &&
        form.customFormResponseI18nDTOS.map(i18nDTO => {
          formNameI18n.push({ language: i18nDTO.language, value: i18nDTO.formName });
          remarkI18n.push({ language: i18nDTO.language, value: i18nDTO.remark });
        });
      form.formNameI18n = formNameI18n;
      form.remarkI18n = remarkI18n;
      this.setState({ form });
    }
  }

  componentDidMount() {
    const { form, formType, propertyList } = this.context;
    if (formType === 2005) {
      propertyList.map(property => {
        if (property.propertyName == 'loan.application.participation.repay.enable')
          this.props.form.setFieldsValue({
            associatePayExpense: property.propertyValue === 'true',
          });

        if (property.propertyName == 'loan.application.configuration') {
          let loanConfiguration = JSON.parse(property.propertyValue);
          if (loanConfiguration.applicationType !== '1003') {
            if (loanConfiguration.applicationType === '1004') {
              this.setState({
                applicationTypeCheckedList: [],
              });
            } else {
              this.setState({
                applicationTypeCheckedList: [loanConfiguration.applicationType],
              });
            }
          }
          this.setState({
            outApplicationAmount: loanConfiguration.outApplicationAmount,
            isReference: loanConfiguration.isReference,
            isExistLoanRelated: true,
            isHasLoanRelated: true,
          });
        }
      });
    }
    if (form) {
      this.props.form.setFieldsValue({
        valid: form.valid,
        formName: form.formName,
        remark: form.remark,
        allowWithdraw: form.allowWithdraw || true,
      });
    }

    //获取单据类型
    this.getSystemValueList('SYS_APPROVAL_FORM_TYPE').then(res => {
      this.setState({
        documentType: res.data.values,
      });
    });
  }

  handleNew = values => {
    let { formType, booksID } = this.context;
    let {
      form,
      applicationTypeCheckedList,
      outApplicationAmount,
      isReference,
      isHasLoanRelated,
      isExistLoanRelated,
    } = this.state;
    let newForm = {
      asSystem: false,
      formType,
      customFormFields: [],
      iconName: '',
      language: this.props.language.code,
      ...form,
      ...values,
    };
    this.processValue(newForm);
    console.log(newForm);
    if (this.validateLength(newForm)) {
      return;
    }
    if (formType === 2005 && isHasLoanRelated && !applicationTypeCheckedList.length) {
      message.error(this.$t('form.setting.form.tip12')); //请选择关联的申请单类型
      return;
    }
    this.setState({ saving: true });
    formService
      .newFormDetail(newForm)
      .then(res => {
        if (formType === 2005) {
          let loanPropertyList = [];
          let repayEnable = {};
          repayEnable.formOid = res.data.formOid;
          repayEnable.propertyName = 'loan.application.participation.repay.enable';
          repayEnable.propertyValue = values.associatePayExpense + '';
          loanPropertyList.push(repayEnable);
          let loanRelate = {};
          loanRelate.formOid = res.data.formOid;
          loanRelate.propertyName = 'loan.application.configuration';
          loanRelate.propertyValue = JSON.stringify({
            outApplicationAmount: outApplicationAmount,
            isReference: isReference,
            applicationType:
              applicationTypeCheckedList.length === 2 ? '1003' : applicationTypeCheckedList[0],
          });
          if (isHasLoanRelated) {
            loanPropertyList.push(loanRelate);
          }
          formService.saveFormProperty(loanPropertyList).then(propertyRes => {});
          //删除借款单关联申请单的表单配置
          if (!isHasLoanRelated && isExistLoanRelated) {
            formService.removeFormProperty(res.data.formOid, ['loan.application.configuration']);
          }
        }
        message.success(this.$t('common.save.success', { name: res.data.formName }));
        this.setState({ saving: false });
        if (booksID && booksID !== ':booksID') {
          this.props.dispatch(
            routerRedux.push({
              pathname: `/admin-setting/form-list/form-list/form-detail/${
                res.data.formOid
              }/${booksID}`,
            })
          );
        } else {
          this.props.dispatch(
            routerRedux.push({
              pathname: `/admin-setting/form-list/form-list/form-detail/${
                res.data.formOid
              }/:booksID`,
            })
          );
        }
        this.props.handleNew(res.data);
      })
      .catch(e => {
        this.setState({ saving: false });
        message.error(this.$t('common.save.filed') + ',' + e.response.data.message);
      });
  };

  //新建／保存前处理数据
  processValue = formData => {
    let { booksID } = this.context;
    if (booksID && booksID !== ':booksID') {
      formData.setOfBooksId = booksID;
    }
    if (formData.associateExpenseReport) {
      formData.associateExpenseReport = true;
    } else {
      formData.associateExpenseReport = false;
    }
    if (this.props.tenantMode) {
      formData.fromType = 2;
    } else {
      formData.fromType = 1;
    }
    formData.i18n = {};
    if (formData.formNameI18n) {
      let formNameLanguageList = [];
      formData.formNameI18n.map(item => {
        if (item.language === 'zh_cn') {
          item.language = 'zh_cn';
        }
        formNameLanguageList.push(item.language);
      });
      //没有的语言value初始化个''
      this.props.languageList.map(languageItem => {
        if (formNameLanguageList.indexOf(languageItem.code) === -1) {
          formData.formNameI18n.push({
            language: languageItem.code,
            value: '',
          });
        }
      });
      formData.i18n.formName = formData.formNameI18n;
    }
    if (formData.remarkI18n) {
      let remarkLanguageList = [];
      formData.remarkI18n.map(item => {
        if (item.language === 'zh_cn') {
          item.language = 'zh_cn';
        }
        remarkLanguageList.push(item.language);
      });
      //没有的语言value初始化个''
      this.props.languageList.map(languageItem => {
        if (remarkLanguageList.indexOf(languageItem.code) === -1) {
          formData.remarkI18n.push({
            language: languageItem.code,
            value: '',
          });
        }
      });
      formData.i18n.remark = formData.remarkI18n;
    }
  };

  //新建／保存前校验多语言长度
  validateLength = formData => {
    let isOverLength = false; //是否超过字符长度
    let errorMessage = '';
    if (formData.i18n.formName) {
      formData.i18n.formName.map(item => {
        if (item.value && item.value.length > 50) {
          errorMessage = this.$t('common.max.characters.length', { max: 50 });
          isOverLength = true;
        }
      });
    }
    if (formData.i18n.remark) {
      formData.i18n.remark.map(item => {
        if (item.value && item.value.length > 50) {
          errorMessage = this.$t('common.max.characters.length', { max: 50 });
          isOverLength = true;
        }
      });
    }
    if (isOverLength) {
      message.error(errorMessage);
    }
    return isOverLength;
  };

  handleSave = e => {
    e.preventDefault();
    let { form, formOid, formType } = this.context;
    let {
      userValue,
      expenseTypeValue,
      visibleCompanyScope,
      companySelectedList,
      applicationTypeCheckedList,
      outApplicationAmount,
      isReference,
      isHasLoanRelated,
      isExistLoanRelated,
    } = this.state;

    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        if (form) {
          let serviceArray = [];
          form.i18n = {
            formName: form.formNameI18n,
            remark: form.remarkI18n,
          };
          form.valid = values.valid;
          if (formType === 2005) {
            if (isHasLoanRelated && !applicationTypeCheckedList.length) {
              message.error(this.$t('form.setting.form.tip12')); //请选择关联的申请单类型
              return;
            }
            let loanPropertyList = [];
            let repayEnable = {};
            repayEnable.formOid = form.formOid;
            repayEnable.propertyName = 'loan.application.participation.repay.enable';
            repayEnable.propertyValue = values.associatePayExpense + '';
            loanPropertyList.push(repayEnable);
            let loanRelate = {};
            loanRelate.formOid = form.formOid;
            loanRelate.propertyName = 'loan.application.configuration';
            loanRelate.propertyValue = JSON.stringify({
              outApplicationAmount: outApplicationAmount,
              isReference: isReference,
              applicationType:
                applicationTypeCheckedList.length === 2 ? '1003' : applicationTypeCheckedList[0],
            });
            if (isHasLoanRelated) {
              loanPropertyList.push(loanRelate);
            }
            serviceArray.push(formService.saveFormProperty(loanPropertyList));
            //删除借款单关联申请单的表单配置
            if (!isHasLoanRelated && isExistLoanRelated) {
              serviceArray.push(
                formService.removeFormProperty(form.formOid, ['loan.application.configuration'])
              );
            }
          }
          form.withdrawFlag = values.withdrawFlag;
          form.withdrawRule = values.withdrawRule;
          let result = {
            approvalFormDTO: form,
          };
          this.processValue(result.approvalFormDTO);
          if (this.validateLength(result.approvalFormDTO)) {
            return;
          }
          this.setState({ saving: true });
          serviceArray.push(formService.saveForm(result));
          Promise.all(serviceArray)
            .then(res => {
              message.success(this.$t('common.save.success', { name: form.formName }));
              this.setState({ saving: false });
              this.props.refreshBase(form.formOid);
            })
            .catch(e => {
              let error = e.response.data;
              if (error.validationErrors && error.validationErrors.length) {
                message.error(
                  `${this.$t('common.save.filed')}，${error.validationErrors[0].message}`
                );
              } else {
                message.error(`${this.$t('common.save.filed')}，${error.message}`);
              }
              this.setState({ saving: false });
            });
        } else {
          this.handleNew(values);
        }
      }
    });
  };
  handleChangePermissions = values => {
    this.setState({ userValue: values });
  };

  handleChangeExpense = value => {
    this.setState({ expenseTypeValue: values });
  };

  handleLanguageInput = (name, i18n, attr) => {
    let { form } = this.state;
    form[attr] = name;
    form[attr + 'I18n'] = i18n;
    this.setState({ form });
  };

  handleChangeCompany = e => {
    this.setState(
      {
        visibleCompanyScope: e.target.value,
      },
      () => {
        this.setState({ companySelectedList: [] });
      }
    );
  };

  handleSelectCompany = value => {
    this.setState({
      companySelectedList: value,
    });
  };

  handleChangeApplicationType = values => {
    this.setState({
      applicationTypeCheckedList: values,
    });
  };

  handleChangeCheckbox = (value, key) => {
    this.setState({
      [key]: value,
    });
  };
  goBack = () => {
    let { booksID } = this.context;
    this.props.dispatch(
      routerRedux.push({
        pathname: `/admin-setting/form-list/form-list/${booksID}`,
      })
    );
  };

  allowWithdrawChange = value => {
    if (value) {
      this.props.form.setFieldsValue({ withdrawMode: 1001 });
    } else {
      this.props.form.setFieldsValue({ withdrawMode: '' });
    }
  };

  render() {
    const { formType, formOid } = this.context;
    const { getFieldDecorator } = this.props.form;
    const { form, documentType, saving, associateExpenseReport } = this.state;
    const formItemLayout = {
      labelCol: { span: 4 },
      wrapperCol: { span: 8 },
    };

    return (
      <div style={{ marginTop: 20 }} className="form-detail-base">
        <Form onSubmit={this.handleSave}>
          <FormItem {...formItemLayout} label={this.$t('form.setting.type') /*类型*/}>
            <span>
              {
                (documentType.find(item => item.value.toString() === formType.toString()) || {})
                  .name
              }
            </span>
          </FormItem>
          {form.associateExpenseReport && (
            <FormItem {...formItemLayout} label={this.$t('form.setting.related.form') /*关联单据*/}>
              <span>
                {associateExpenseReport.fetched ? associateExpenseReport.data.formName : <Spin />}
              </span>
            </FormItem>
          )}
          <FormItem {...formItemLayout} label={this.$t('form.setting.status') /*状态*/}>
            {getFieldDecorator('valid', {
              valuePropName: 'checked',
              initialValue: true,
            })(<Switch disabled={form.fromType == 2 && !this.props.tenantMode} />)}
          </FormItem>
          <FormItem {...formItemLayout} label={this.$t('form.setting.form.name') /*表单名称*/}>
            {getFieldDecorator('formName', {
              rules: [
                {
                  required: true,
                  message: this.$t('common.please.enter'), //请输入
                },
                {
                  max: 50,
                  message: this.$t('common.max.characters.length', { max: 50 }),
                },
              ],
              initialValue: '',
            })(
              <LanguageInput
                nameChange={(value, i18n) => this.handleLanguageInput(value, i18n, 'formName')}
                width={'100%'}
                name={form.formName}
                placeholder={this.$t('common.max.characters.length', { max: 50 })}
                isEdit={formOid}
                disabled={form.fromType == 2 && !this.props.tenantMode}
                i18nName={form.formNameI18n}
              />
            )}
            <div style={{ color: '#989898' }}>
              {this.$t('form.setting.form.tip01') /*名称可重复，请避免员工看到重名表单*/}
            </div>
          </FormItem>
          <FormItem {...formItemLayout} label={this.$t('form.setting.remark') /*备注*/}>
            {getFieldDecorator('remark', {
              rules: [
                {
                  max: 50,
                  message: this.$t('common.max.characters.length', { max: 50 }),
                },
              ],
              initialValue: '',
            })(
              <LanguageInput
                nameChange={(value, i18n) => this.handleLanguageInput(value, i18n, 'remark')}
                width={'100%'}
                name={form.remark}
                placeholder={this.$t('common.max.characters.length', { max: 50 })}
                isEdit={formOid}
                disabled={form.fromType == 2 && !this.props.tenantMode}
                i18nName={form.remarkI18n}
              />
            )}
          </FormItem>
          <FormItem {...formItemLayout} label="允许撤回">
            {getFieldDecorator('withdrawFlag', {
              initialValue: form.withdrawFlag,
              valuePropName: 'checked',
            })(
              <Switch
                onChange={this.allowWithdrawChange}
                checkedChildren="启用"
                unCheckedChildren="禁用"
              />
            )}
          </FormItem>
          <FormItem {...formItemLayout} label="撤回模式">
            {getFieldDecorator('withdrawRule', {
              initialValue: form.withdrawRule || 1001,
              rules: [
                {
                  required: this.props.form.getFieldValue('allowWithdraw'),
                  message: this.$t('common.please.select'), //请输入
                },
              ],
            })(
              <Select disabled={!this.props.form.getFieldValue('withdrawFlag')}>
                <Select.Option value={1001}>无审批记录时可撤回</Select.Option>
                <Select.Option value={1002}>审批流未结束均可撤回</Select.Option>
              </Select>
            )}
          </FormItem>
          <Row style={{ marginTop: 12, marginBottom: 20 }}>
            <Col span={8} offset={5}>
              {(form.fromType != 2 || this.props.tenantMode) && (
                <Button htmlType="submit" type="primary" loading={saving}>
                  {this.$t('common.save') /*保存*/}
                </Button>
              )}
            </Col>
          </Row>
        </Form>
        <div style={{ paddingLeft: '20px' }}>
          <div
            style={{
              position: 'fixed',
              bottom: 0,
              marginLeft: '-35px',
              width: '100%',
              height: '50px',
              boxShadow: '0px -5px 5px rgba(0, 0, 0, 0.067)',
              background: '#fff',
              lineHeight: '50px',
              zIndex: 1,
            }}
          >
            <Button type="primary" onClick={this.goBack} style={{ margin: '0 20px' }}>
              {this.$t('common.back' /*提 交*/)}
            </Button>
          </div>
        </div>
      </div>
    );
  }
}

FormDetailBase.propTypes = {
  handleNew: PropTypes.func,
  refreshBase: PropTypes.func,
};

function mapStateToProps(state) {
  return {
    company: state.user.company,
    language: state.languages.languages,
    languageList: state.languages.languageList,
    tenantMode: true,
  };
}

FormDetailBase.contextTypes = {
  formType: PropTypes.any,
  formOid: PropTypes.string,
  booksID: PropTypes.string,
  form: PropTypes.object,
  expenseTypeScope: PropTypes.object,
  userScope: PropTypes.object,
  propertyList: PropTypes.array,
  // router: React.PropTypes.object
};

const WrappedFormDetailBase = Form.create()(FormDetailBase);

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(WrappedFormDetailBase);

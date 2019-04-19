import React from 'react';
import { message, Form, Input, Card, Select, DatePicker, Row, Col } from 'antd';
import { connect } from 'dva';
import Chooser from 'widget/chooser';
import moment from 'moment';
import Lov from 'widget/Template/lov';
// import AccountOpenEntryInfo from './account-open-entry-info';
import MaintenanceeDetailForm from '../account-open-maintenance/maintenance-detail-form';
// import MaintenanceeDetailForm from './maintenance-detail-form';
import accountOpenEntryService from './account-open-entry.service';
// /* eslint-disable */
const { Option } = Select;
const FormItem = Form.Item;
class AccountOpenEntryDetail extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      isNew: true, // 是否为新建单据
      formData: {},
      bankOptions: [], // 开户银行列表
      bankBranchList: [], // 银行分支所有信息
      currencyList: [], // 币种
      porosaveSuccess: false,
    };
  }

  componentDidMount() {
    const { match } = this.props;
    if (match.params.id) {
      this.setState({
        isNew: false,
      });
      this.getAccountOpenMaintenanceDetail(match.params.id);
    }
    this.getAccountBank();
    this.getCurrencyList();
  }

  /**
   * 保存
   */
  entrySave = params => {
    const { form, user } = this.props;
    const { bankBranchList } = this.state;
    form.validateFields((err, values) => {
      if (!err) {
        const headerInfo = {
          ...params,
          requisitionDate: moment(values.requisitionDate).format(),
          employeeId: user.id,
          currencyCode: values.currencyCode,
          remarks: values.remarks,
          companyId: values.company[0].id,
          departmentId: values.department[0].departmentId || '',
          openProvince: bankBranchList.provinceCode,
          openCity: bankBranchList.cityCode,
          openBank: values.opneBank.key,
          branchBank: values.bankBranch.bankCode,
          branchBankName: values.bankBranch.bankBranchName,
          // attachmentOid: uploadOIDs.join(','),
        };
        accountOpenEntryService
          .createOrUpdateList(headerInfo)
          .then(res => {
            if (res.status === 200) {
              this.setState({
                porpversionNumber: res.data.versionNumber,
                porpkeyversionNumber: res.data.acountUkeyInfo.versionNumber,
                porosaveSuccess: true,
              });
              message.success('保存成功！');
              this.onFlash(res.data.id);
            }
          })
          .catch(error => {
            message.error(error.errorCode);
          });
      }
    });
  };

  /**
   * 保存后，重新加载数据
   */
  onFlash = id => {
    accountOpenEntryService
      .getAccountOpenMaintenanceDetail(id)
      .then(res => {
        this.setState({
          formData: res.data,
          backInformation: res.data.documentNumber,
        });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  /**
   * 获取开户银行列表
   */
  getAccountBank = () => {
    accountOpenEntryService
      .getAccountBank()
      .then(res => {
        if (res.data.length > 0) {
          this.setState({
            bankOptions: res.data,
          });
        }
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  onChange = values => {
    this.setState({
      bankBranchList: values,
    });
  };

  onSelect = value => {
    console.log('1', value);
    const { form } = this.props;
    form.setFields({
      bankBranch: {
        value: {},
      },
    });
  };

  /**
   * 获取币种
   */
  getCurrencyList = () => {
    const { company } = this.props;
    accountOpenEntryService
      .getCurrencyList(company.companyOid)
      .then(res => {
        this.setState({ currencyList: res.data });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  /**
   * 根据ID查询账户开户维护详情
   */
  getAccountOpenMaintenanceDetail = id => {
    accountOpenEntryService
      .getAccountOpenMaintenanceDetail(id)
      .then(res => {
        this.setState({
          formData: res.data,
        });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  render() {
    const {
      form,
      form: { getFieldDecorator },
      company,
      user,
    } = this.props;
    const bankCode1 = form.getFieldValue('opneBank') ? form.getFieldValue('opneBank').key : '';
    const {
      isNew,
      bankOptions,
      currencyList,
      formData,
      porpversionNumber,
      backInformation,
      porpkeyversionNumber,
      porosaveSuccess,
    } = this.state;
    const formItemLayout = {
      labelCol: {
        span: 8,
      },
      wrapperCol: {
        span: 16,
      },
    };
    return (
      <div>
        <Card
          style={{
            boxShadow: '0 2px 2px rgba(0, 0, 0, 0.15)',
            marginRight: 15,
            marginLeft: 15,
            marginTop: 1,
          }}
        >
          <Form>
            {/* <section style={{ paddingTop: '15px' }}> */}
            <Row>
              <Col span={6}>
                <FormItem label="单据编号" {...formItemLayout}>
                  {getFieldDecorator('documentNumber', {
                    initialValue: isNew ? backInformation : formData.documentNumber,
                  })(<Input disabled />)}
                </FormItem>
              </Col>
              <Col span={6}>
                <FormItem label="申请公司" {...formItemLayout}>
                  {getFieldDecorator('company', {
                    rules: [{ required: true, message: this.$t('common.please.select') }],
                    initialValue: isNew
                      ? [{ id: company.id, name: company.name }]
                      : [{ id: formData.companyId, name: formData.companyName }],
                  })(
                    <Chooser
                      type="company"
                      labelKey="name"
                      valueKey="id"
                      showClear={false}
                      single
                      listExtraParams={{ setOfBooksId: company.setOfBooksId }}
                    />
                  )}
                </FormItem>
              </Col>
              <Col span={6}>
                <FormItem label="申请部门" {...formItemLayout}>
                  {getFieldDecorator('department', {
                    rules: [{ required: true, message: this.$t('common.please.select') }],
                    initialValue: isNew
                      ? [{ departmentId: user.departmentId, path: user.departmentName }]
                      : [{ departmentId: formData.departmentId, path: formData.departmentName }],
                  })(
                    <Chooser
                      type="department_document"
                      labelKey="path"
                      valueKey="departmentId"
                      single
                      listExtraParams={{ tenantId: user.tenantId }}
                    />
                  )}
                </FormItem>
              </Col>
              <Col span={6}>
                <FormItem {...formItemLayout} label="开户银行">
                  {getFieldDecorator('opneBank', {
                    rules: [{ required: true, message: this.$t('common.please.select') }],
                    initialValue: isNew
                      ? {}
                      : { key: formData.openBank, label: formData.openBankName },
                  })(
                    <Select labelInValue placeholder="请选择" allowClear onSelect={this.onSelect}>
                      {bankOptions.map(option => {
                        return <Option key={option.value}>{option.name}</Option>;
                      })}
                    </Select>
                  )}
                </FormItem>
              </Col>
            </Row>
            <Row>
              <Col span={6}>
                <FormItem label="分支行信息" {...formItemLayout}>
                  {getFieldDecorator('bankBranch', {
                    rules: [{ required: true, message: this.$t('common.please.select') }],
                    initialValue: isNew
                      ? {}
                      : {
                          bankCode: formData.branchBank,
                          bankBranchName: formData.branchBankName,
                        },
                  })(
                    <Lov
                      code="bankbranch_choose"
                      valueKey="id"
                      labelKey="bankBranchName"
                      single
                      onChange={this.onChange}
                      extraParams={{ bankHead: bankCode1 }}
                    />
                  )}
                </FormItem>
              </Col>
              <Col span={6}>
                <FormItem label="币种" {...formItemLayout}>
                  {getFieldDecorator('currencyCode', {
                    rules: [{ required: true, message: this.$t('common.please.select') }],
                    initialValue: isNew ? company.baseCurrency : formData.currencyCode,
                  })(
                    <Select disabled={!isNew} onChange={this.currencyChange} allowClear>
                      {currencyList.map(item => {
                        return (
                          <Select.Option key={item.currency} value={item.currency}>
                            {item.currency}-{item.currencyName}
                          </Select.Option>
                        );
                      })}
                    </Select>
                  )}
                </FormItem>
              </Col>
              <Col span={6}>
                <FormItem label="申请日期" {...formItemLayout}>
                  {getFieldDecorator('requisitionDate', {
                    rules: [{ required: true }],
                    initialValue: isNew ? moment() : moment(formData.requisitionDate),
                  })(<DatePicker disabled format="YYYY-MM-DD" />)}
                </FormItem>
              </Col>
              <Col span={6}>
                <FormItem label="申请人" {...formItemLayout}>
                  {getFieldDecorator('employeeName', {
                    rules: [{ required: true }],
                    initialValue: isNew ? user.userName : formData.employeeName,
                  })(<Input disabled />)}
                </FormItem>
              </Col>
            </Row>
            <Row>
              {/* <Col span={6}>
                <FormItem label="开户省" {...formItemLayout}>
                  {getFieldDecorator('openProvince', {
                    rules: [{ required: true }],
                    initialValue: isNew ? user.userName : formData.employeeName,
                  })(<Input disabled />)}
                </FormItem>
              </Col>
              <Col span={6}>
                <FormItem label="开户市" {...formItemLayout}>
                  {getFieldDecorator('openCity', {
                    rules: [{ required: true }],
                    initialValue: isNew ? user.userName : formData.employeeName,
                  })(<Input disabled />)}
                </FormItem>
              </Col> */}
              <Col span={6}>
                <FormItem label="备注" {...formItemLayout}>
                  {getFieldDecorator('remarks', {
                    initialValue: isNew ? '' : formData.remarks,
                  })(<Input style={{ width: '455px' }} />)}
                </FormItem>
              </Col>
            </Row>
          </Form>
        </Card>
        <MaintenanceeDetailForm
          formData={formData}
          entrySave={this.entrySave}
          from="openEntryList"
          porpversionNumber={porpversionNumber}
          porpkeyversionNumber={porpkeyversionNumber}
          porosaveSuccess={porosaveSuccess}
        />
        {/* <MaintenanceeDetailForm formData={formData} from="openMaintenanceList" /> */}
        {/* <div style={{ textAlign: 'right' }}>
          <a
            style={{ fontSize: '14px', paddingBottom: '20px', float: 'left' }}
            onClick={this.onClickBack}
          >
            <Icon type="rollback" style={{ marginRight: '5px' }} />返回
          </a>
          {(
            <div style={{ float: 'right' }}>
              <Button type="primary" style={{ margin: '0 20px' }} onClick={this.saveFormData}>
                保存
              </Button>
              <Button type="primary" onClick={this.submitDetail} style={{ margin: '0 20px' }}>
                提交
              </Button>
            </div>
          )}
        </div> */}
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
  };
}

export default connect(mapStateToProps)(Form.create()(AccountOpenEntryDetail));

import React, { Component } from 'react';
import { messages } from 'utils/utils';
import { message, Form, Select, DatePicker, Button, Spin } from 'antd';
import Chooser from 'components/Widget/chooser';
import { connect } from 'dva';
import moment from 'moment';
import config from 'config';
import httpFetch from 'share/httpFetch';
import 'styles/setting/params-setting/params-setting.scss';
import service from './centralized-authorization-servce';
import Lov from 'widget/Template/lov';

const FormItem = Form.Item;
const Option = Select.Option;

class NewCentralized extends Component {
  constructor(props) {
    super(props);
    this.state = {
      saveLoading: false,
      loading: false,
      isDisable: true,
      formList: [],
      setOfBooksId: this.props.company.setOfBooksId,
      lov: {
        departmentId: null,
        companyId: null,
      },
    };
  }
  // 公司变化更改
  changeCompanyLov = e => {
    if (this.props.company.companyUnitFlag) {
      let { lov } = this.state;
      lov.companyId = e.id;
      this.setState({ lov });
    }
  };
  setCompanyAndDepartmentNull = () => {
    let { lov } = this.state;
    lov.companyId = null;
    (lov.departmentId = null), this.setState({ lov });
  };
  // 公司变化更改
  changeDepartmentLov = e => {
    if (this.props.company.companyUnitFlag) {
      let { lov } = this.state;
      lov.departmentId = e.id;
      this.setState({ lov });
    }
  };

  // 生命周期
  componentDidMount() {
    const { documentCategory } = this.props.params;
    let list = [];
    if (documentCategory === '801008') {
      //核算工单
      httpFetch
        .get(
          `${
            config.accountingUrl
          }/api/general/ledger/work/order/types/query?page=0&size=1000&setOfBooksId=` +
            this.state.setOfBooksId
        )
        .then(({ data }) => {
          data.map(item => {
            list.push({ value: item.id, label: `${item.workOrderTypeName}` });
          });
          this.setState({ formList: list });
        })
        .catch(err => {
          message.error(err.response.message);
        });
    } else if (documentCategory === '801005') {
      //付款申请单类型定义
      httpFetch
        .get(
          `${config.payUrl}/api/acp/request/type/query?page=0&size=1000&setOfBooksId=` +
            this.state.setOfBooksId
        )
        .then(({ data }) => {
          data.map(item => {
            list.push({ value: item.id, label: `${item.typeName}` });
          });
          this.setState({ formList: list });
        })
        .catch(err => {
          message.error(err.response.message);
        });
    } else if (documentCategory === '801006') {
      //费用调整单类型定义
      httpFetch
        .get(
          `${config.expenseUrl}/api/expense/adjust/types/query?page=0&size=1000&setOfBooksId=` +
            this.state.setOfBooksId
        )
        .then(({ data }) => {
          data.map(item => {
            list.push({ value: item.id, label: `${item.expAdjustTypeName}` });
          });
          this.setState({ formList: list });
        })
        .catch(err => {
          message.error(err.response.message);
        });
    } else if (documentCategory === '801003') {
      //预付款单类型定义
      httpFetch
        .get(
          `${
            config.prePaymentUrl
          }/api/cash/pay/requisition/types/query?&page=0&size=1000&setOfBookId=` +
            this.state.setOfBooksId
        )
        .then(({ data }) => {
          data.map(item => {
            list.push({ value: item.id, label: `${item.typeName}` });
          });
          this.setState({ formList: list });
        })
        .catch(err => {
          message.error(err.response.message);
        });
    } else if (documentCategory === '801004') {
      //合同类型定义
      httpFetch
        .get(
          `${config.contractUrl}/api/contract/type/query?page=0&size=1000&setOfBooksId=` +
            this.state.setOfBooksId
        )
        .then(({ data }) => {
          data.map(item => {
            list.push({ value: item.id, label: `${item.contractTypeName}` });
          });
          this.setState({ formList: list });
        })
        .catch(err => {
          message.error(err.response.message);
        });
    } else if (documentCategory === '801009') {
      //费用申请单类型定义
      httpFetch
        .get(
          `${config.expenseUrl}/api/expense/application/type/query?page=0&size=1000&setOfBooksId=` +
            this.state.setOfBooksId
        )
        .then(({ data }) => {
          data.map(item => {
            list.push({ value: item.id, label: `${item.typeName}` });
          });
          this.setState({ formList: list });
        })
        .catch(err => {
          message.error(err.response.message);
        });
    } else if (documentCategory === '801001') {
      //报账单
      httpFetch
        .get(
          `${config.expenseUrl}/api/expense/report/type/query?page=0&size=1000&setOfBooksId=` +
            this.state.setOfBooksId
        )
        .then(({ data }) => {
          data.map(item => {
            list.push({ value: item.id, label: `${item.formName}` });
          });
          this.setState({ formList: list });
        })
        .catch(err => {
          message.error(err.response.message);
        });
    } else if (documentCategory === '801002') {
      //预算日记账
      httpFetch
        .get(
          `${config.budgetUrl}/api/budget/journal/types/query/setOfBooksId?setOfBooksId=` +
            this.state.setOfBooksId
        )
        .then(({ data }) => {
          data.map(item => {
            list.push({ value: item.id, label: `${item.journalTypeName}` });
          });
          this.setState({ formList: list });
        })
        .catch(err => {
          message.error(err.response.message);
        });
    } else {
      this.setState({ formList: [] });
    }
  }

  // 提交
  handleSave = e => {
    e.preventDefault();
    let { params } = this.props;
    this.props.form.validateFields((err, value) => {
      if (err) return;
      if (value.endDate !== null) {
        let end = moment(value.endDate).format('YYYY-MM-DD');
        let start = moment(value.startDate).format('YYYY-MM-DD');
        if (end < start) {
          message.warn('有效日期至不能小于有效日期从！');
          return;
        }
      }
      this.setState({ saveLoading: true });
      let param = {
        ...value,
        baileeId: value.baileeId[0].userId,
        formId: value.formId ? value.formId.key : '',
        companyId: value.companyId ? value.companyId.id : '',
        unitId: value.unitId ? value.unitId.id : '',
        mandatorId: value.mandatorId ? value.mandatorId[0].userId : '',
        tenantId: params.tenantId,
        versionNumber: params.versionNumber,
        setOfBooksId: this.props.company.setOfBooksId,
      };
      this.setState({ saveLoading: false });
      let handleMethod = '';
      let messageValue = '';
      if (this.props.params.id) {
        messageValue = '编辑成功';
        handleMethod = service.editCentralizedSetting;
        param.id = this.props.params.id;
      } else {
        messageValue = '新增成功';
        handleMethod = service.addCentralizedSetting;
      }
      handleMethod &&
        handleMethod(param)
          .then(res => {
            message.success(messageValue);
            this.setState({ saveLoading: false }, () => {
              this.props.close(true);
            });
          })
          .catch(err => {
            message.error(err.response.data.message);
            this.setState({ saveLoading: false });
          });
    });
  };

  //取消
  handleCancel = e => {
    this.props.close && this.props.close(e);
  };

  // 单据类型可选
  changDisabled = (value, label) => {
    let list = [];
    if (value) {
      this.props.form.setFieldsValue({ formId: '' });
      this.setState({ isDisable: false, formList: [], loading: true });
      if (value === '801008') {
        //核算工单
        httpFetch
          .get(
            `${
              config.accountingUrl
            }/api/general/ledger/work/order/types/query?page=0&size=1000&setOfBooksId=` +
              this.state.setOfBooksId
          )
          .then(({ data }) => {
            data.map(item => {
              list.push({ value: item.id, label: `${item.workOrderTypeName}` });
            });
            this.setState({ formList: list, loading: false });
          })
          .catch(err => {
            message.error(err.response.message);
          });
      } else if (value === '801003') {
        //预付款单类型定义
        httpFetch
          .get(
            `${
              config.prePaymentUrl
            }/api/cash/pay/requisition/types/query?&page=0&size=1000&setOfBookId=` +
              this.state.setOfBooksId
          )
          .then(({ data }) => {
            data.map(item => {
              list.push({ value: item.id, label: `${item.typeName}` });
            });
            this.setState({ formList: list, loading: false });
          })

          .catch(err => {
            message.error(err.response.message);
          });
      } else if (value === '801006') {
        //费用调整单类型定义
        httpFetch
          .get(
            `${config.expenseUrl}/api/expense/adjust/types/query?page=0&size=1000&setOfBooksId=` +
              this.state.setOfBooksId
          )
          .then(({ data }) => {
            data.map(item => {
              list.push({ value: item.id, label: `${item.expAdjustTypeName}` });
            });
            this.setState({ formList: list, loading: false });
          })
          .catch(err => {
            message.error(err.response.message);
          });
      } else if (value === '801005') {
        //付款申请单类型定义
        httpFetch
          .get(
            `${config.payUrl}/api/acp/request/type/query?page=0&size=1000&setOfBooksId=` +
              this.state.setOfBooksId
          )
          .then(({ data }) => {
            data.map(item => {
              list.push({ value: item.id, label: `${item.typeName}` });
            });
            this.setState({ formList: list, loading: false });
          })

          .catch(err => {
            message.error(err.response.message);
          });
      } else if (value === '801004') {
        //合同类型定义
        httpFetch
          .get(
            `${config.contractUrl}/api/contract/type/query?page=0&size=1000&setOfBooksId=` +
              this.state.setOfBooksId
          )
          .then(({ data }) => {
            data.map(item => {
              list.push({ value: item.id, label: `${item.contractTypeName}` });
            });
            this.setState({ formList: list, loading: false });
          })
          .catch(err => {
            message.error(err.response.message);
          });
      } else if (value === '801009') {
        //费用申请单类型定义
        httpFetch
          .get(
            `${
              config.expenseUrl
            }/api/expense/application/type/query?page=0&size=1000&setOfBooksId=` +
              this.state.setOfBooksId
          )
          .then(({ data }) => {
            data.map(item => {
              list.push({ value: item.id, label: `${item.typeName}` });
              this.setState({ formList: list, loading: false });
            });
          })
          .catch(err => {
            message.error(err.response.message);
          });
      } else if (value === '801001') {
        //报账单
        httpFetch
          .get(
            `${config.expenseUrl}/api/expense/report/type/query?page=0&size=1000&setOfBooksId=` +
              this.state.setOfBooksId
          )
          .then(({ data }) => {
            data.map(item => {
              list.push({ value: item.id, label: `${item.formName}` });
            });
            this.setState({ formList: list, loading: false });
          })
          .catch(err => {
            message.error(err.response.message);
          });
      } else if (value === '801002') {
        //报账单
        httpFetch
          .get(
            `${config.budgetUrl}/api/budget/journal/types/query/setOfBooksId?setOfBooksId=` +
              this.state.setOfBooksId
          )
          .then(({ data }) => {
            data.map(item => {
              list.push({ value: item.id, label: `${item.journalTypeName}` });
            });
            this.setState({ formList: list, loading: false });
          })
          .catch(err => {
            message.error(err.response.message);
          });
      } else {
        this.setState({ formList: [] });
      }
    } else {
      this.props.form.setFieldsValue({ formId: '' });
      this.setState({ isDisable: true, formList: [] });
    }
  };

  //改变账套后清除原账套下的表单数据
  clearFormData = value => {
    this.props.form.setFieldsValue({
      formId: '',
      documentCategory: '',
      companyId: '',
      unitId: '',
      mandatorId: '',
      baileeId: '',
      startDate: '',
      endDate: '',
    });
    this.setState({ setOfBooksId: value });
    this.setCompanyAndDepartmentNull();
  };

  render() {
    const { getFieldDecorator } = this.props.form;
    const formItemLayout = {
      labelCol: { span: 6, offset: 1 },
      wrapperCol: { span: 10, offset: 1 },
    };
    const { saveLoading, isDisable, formList, loading } = this.state;
    const { setOfBooks, setOfBills, params, form, setOfBooksId } = this.props;

    return (
      <div>
        <Form onSubmit={this.handleSave}>
          <FormItem {...formItemLayout} label="账套">
            {getFieldDecorator('setOfBooksId', {
              rules: [
                {
                  required: true,
                  message: this.$t({ id: 'common.please.enter' }),
                },
              ],
              initialValue: params.id ? params.setOfBooksId : setOfBooksId,
            })(
              <Select onChange={value => this.clearFormData(value)}>
                {setOfBooks.map(item => {
                  return (
                    <Option key={item.value} value={item.value}>
                      {item.label}
                    </Option>
                  );
                })}
              </Select>
            )}
          </FormItem>
          <FormItem {...formItemLayout} label="单据大类">
            {getFieldDecorator('documentCategory', {
              rules: [
                {
                  required: true,
                  message: this.$t({ id: 'common.please.enter' }),
                },
              ],
              initialValue: params.id ? params.documentCategory : '',
            })(
              <Select onChange={this.changDisabled}>
                {setOfBills.map(item => {
                  return (
                    <Select.Option key={item.value} value={item.value}>
                      {item.label}
                    </Select.Option>
                  );
                })}
              </Select>
            )}
          </FormItem>
          <FormItem {...formItemLayout} label="单据类型">
            {getFieldDecorator('formId', {
              rules: [
                {
                  required: false,
                  message: this.$t({ id: 'common.please.enter' }),
                },
              ],
              initialValue: params.id ? { key: params.formId, label: params.formName } : '',
            })(
              <Select
                disabled={isDisable && (params.id ? false : true)}
                labelInValue
                allowClear
                notFoundContent={
                  loading ? <Spin size="small" /> : messages('agency.setting.no.result')
                }
              >
                {formList.map(form => {
                  return (
                    <Select.Option key={form.value} value={form.value}>
                      {form.label}
                    </Select.Option>
                  );
                })}
              </Select>
            )}
          </FormItem>
          <FormItem {...formItemLayout} label="公司">
            {getFieldDecorator('companyId', {
              rules: [
                {
                  required: false,
                  message: this.$t({ id: 'common.please.enter' }),
                },
              ],
              initialValue:
                params.companyId && params.id
                  ? { id: params.companyId, companyName: params.companyName }
                  : {},
            })(
              <Lov
                code="company_lov"
                valueKey="id"
                labelKey="companyName"
                allowClear={true}
                onChange={this.changeCompanyLov}
                single
                extraParams={{
                  enbaled: true,
                  departmentId: this.state.lov.departmentId,
                  setOfBooksId: form.getFieldValue('setOfBooksId') || '',
                }}
              />
            )}
          </FormItem>
          <FormItem {...formItemLayout} label="部门">
            {getFieldDecorator('unitId', {
              rules: [
                {
                  required: false,
                  message: this.$t({ id: 'common.please.enter' }),
                },
              ],
              initialValue:
                params.id && params.unitId
                  ? { departmentOid: params.unitId, departmentName: params.unitName }
                  : {},
            })(
              <Lov
                code="department_lov"
                valueKey="departmentOid"
                labelKey="departmentName"
                allowClear={true}
                onChange={this.changeDepartmentLov}
                single
                extraParams={{
                  status: 101,
                  companyId: this.state.lov.companyId,
                  setOfBooksId: this.props.company.companyUnitFlag
                    ? form.getFieldValue('setOfBooksId') || ''
                    : null,
                }}
              />
            )}
          </FormItem>
          <FormItem {...formItemLayout} label="委托人">
            {getFieldDecorator('mandatorId', {
              rules: [
                {
                  required: false,
                  message: this.$t({ id: 'common.please.enter' }),
                },
              ],
              initialValue:
                params.id && params.mandatorId
                  ? [
                      {
                        userId: params.mandatorId,
                        userName: params.mandatorName,
                        userCode: params.mandatorCode,
                      },
                    ]
                  : '',
            })(
              <Chooser
                type="select_authorization_user"
                // labelKey="userName"
                labelKey="${userCode}-${userName}"
                valueKey="userId"
                single={true}
              />
            )}
          </FormItem>
          <FormItem {...formItemLayout} label="受托人">
            {getFieldDecorator('baileeId', {
              rules: [
                {
                  required: true,
                  message: this.$t({ id: 'common.please.enter' }),
                },
              ],
              initialValue:
                params.id && params.baileeId
                  ? [
                      {
                        userId: params.baileeId,
                        userName: params.baileeName,
                        userCode: params.baileeCode,
                      },
                    ]
                  : '',
            })(
              <Chooser
                type="select_authorization_user"
                // labelKey="userName"
                labelKey="${userCode}-${userName}"
                valueKey="userId"
                single={true}
              />
            )}
          </FormItem>
          <FormItem {...formItemLayout} label="有效日期从">
            {getFieldDecorator('startDate', {
              rules: [
                {
                  required: true,
                  message: this.$t({ id: 'common.please.enter' }),
                },
              ],
              initialValue: params.startDate ? moment(params.startDate) : '',
            })(<DatePicker style={{ width: '100%' }} />)}
          </FormItem>
          <FormItem {...formItemLayout} label="有效日期至">
            {getFieldDecorator('endDate', {
              rules: [
                {
                  required: false,
                  message: this.$t({ id: 'common.please.enter' }),
                },
              ],
              initialValue: params.endDate ? moment(params.endDate) : '',
            })(<DatePicker style={{ width: '100%' }} />)}
          </FormItem>
          <div className="slide-footer">
            <Button className="btn" type="primary" htmlType="submit" loading={saveLoading}>
              {this.$t('common.save')}
            </Button>
            <Button className="btn" onClick={this.handleCancel}>
              {this.$t('common.cancel')}
            </Button>
          </div>
        </Form>
      </div>
    );
  }
}

const WrappedResponsibility = Form.create()(NewCentralized);
function mapStateToProps(state) {
  return {
    tenantMode: true,
    user: state.user.currentUser,
    company: state.user.company,
    organization: state.user.organization || {},
  };
}

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(WrappedResponsibility);

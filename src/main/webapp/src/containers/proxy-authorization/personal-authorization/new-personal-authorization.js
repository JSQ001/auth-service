import React, { Component } from 'react';
import { messages } from 'utils/utils';
import { Alert, Form, Select, DatePicker, Button, message, Spin } from 'antd';
import Chooser from 'components/Widget/chooser';
import { connect } from 'dva';
import 'styles/setting/params-setting/params-setting.scss';
import moment from 'moment';
import config from 'config';
import httpFetch from 'share/httpFetch';
import Service from './personal-authorization-service';
const FormItem = Form.Item;
const Option = Select.Option;

class NewPersonal extends Component {
  constructor(props) {
    super(props);
    this.state = {
      saveLoading: false,
      loading: false,
      isDisable: true,
      setBooks: [],
      setOfBooksId: this.props.company.setOfBooksId,
    };
  }

  //生命周期
  componentDidMount() {
    const { documentCategory } = this.props.params;
    let list = [];
    let handle;
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
          this.setState({ setBooks: list });
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
          this.setState({ setBooks: list });
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
          this.setState({ setBooks: list });
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
          this.setState({ setBooks: list });
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
          this.setState({ setBooks: list });
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
          this.setState({ setBooks: list });
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
            list.push({ value: item.id, label: `${item.reportTypeName}` });
          });
          this.setState({ setBooks: list });
        })
        .catch(err => {
          message.error(err.response.message);
        });
    } else {
      this.setState({ setBooks: [] });
    }
  }

  // 提交
  handleSave = e => {
    e.preventDefault();
    let { params } = this.props;
    this.props.form.validateFields((err, values, record) => {
      if (err) return;
      this.setState({ saveLoading: true });
      let handleEvent, msg, param;
      if (!params.id) {
        handleEvent = Service.addPersonalSetting;
        msg = '新增成功!';
        param = {
          ...values,
          documentCategory: values.documentCategory ? values.documentCategory : '',
          mandatorId: this.props.user.id,
          baileeId: values.baileeId ? values.baileeId[0].userId : '',
          formId: values.formId ? values.formId.key : '',
        };
      } else {
        handleEvent = Service.editPersonalSetting;
        msg = '编辑成功!';
        param = {
          id: params.id,
          ...values,
          baileeId: values.baileeId ? values.baileeId[0].userId : '',
          formId: values.formId ? values.formId.key : '',
          tenantId: params.tenantId,
          versionNumber: params.versionNumber,
          setOfBooksId: this.props.company.setOfBooksId,
        };
      }
      handleEvent(param)
        .then(res => {
          message.success(msg);
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

  // 单据类型可用
  changDisabled = value => {
    let list = [];
    if (value) {
      this.props.form.setFieldsValue({ formId: '' });
      this.setState({ isDisable: false, loading: true, setBooks: [] });
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
            this.setState({ setBooks: list, loading: false });
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
            this.setState({ setBooks: list, loading: false });
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
            this.setState({ setBooks: list, loading: false });
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
            this.setState({ setBooks: list, loading: false });
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
            this.setState({ setBooks: list, loading: false });
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
              this.setState({ setBooks: list, loading: false });
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
              list.push({ value: item.id, label: `${item.reportTypeName}` });
            });
            this.setState({ setBooks: list, loading: false });
          })
          .catch(err => {
            message.error(err.response.message);
          });
      } else {
        this.setState({ setBooks: [], loading: false });
      }
    } else {
      this.props.form.setFieldsValue({ formId: '' });
      this.setState({ isDisable: true, setBooks: [], loading: false });
    }
  };

  render() {
    const { getFieldDecorator } = this.props.form;
    const formItemLayout = {
      labelCol: { span: 6, offset: 1 },
      wrapperCol: { span: 10, offset: 1 },
    };
    const { saveLoading, isDisable, setBooks, loading } = this.state;
    const { setOfBooks, params } = this.props;

    return (
      <div>
        <Alert message="若不填单据类型，则将单据大类中单据类型全部授权" type="info" showIcon />
        <br />

        <Form onSubmit={this.handleSave}>
          <FormItem {...formItemLayout} label="委托人">
            {getFieldDecorator('mandatorId', {
              rules: [
                {
                  required: true,
                  message: this.$t({ id: 'common.please.enter' }),
                },
              ],
              initialValue: this.props.user.id,
            })(
              <Select disabled>
                <Select.Option value={this.props.user.id}>
                  {this.props.user.remark}-{this.props.user.userName}
                </Select.Option>
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
              initialValue: params.documentCategory ? params.documentCategory : '',
            })(
              <Select allowClear onChange={this.changDisabled}>
                {setOfBooks.map(option => {
                  return (
                    <Select.Option key={option.value} value={option.value}>
                      {option.label}
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
                loading={loading}
                notFoundContent={
                  loading ? <Spin size="small" /> : messages('agency.setting.no.result')
                }
              >
                {setBooks.map(form => {
                  return (
                    <Select.Option key={form.value} value={form.value}>
                      {form.label}
                    </Select.Option>
                  );
                })}
              </Select>
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
              initialValue: params.baileeId
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
              initialValue: params.id ? moment(params.startDate) : '',
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
              initialValue: params.endDate ? moment(params.endDate) : null,
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
function mapStateToProps(state) {
  return {
    tenantMode: true,
    user: state.user.currentUser,
    company: state.user.company,
    organization: state.user.organization || {},
  };
}

const WrappedNewBuilt = Form.create()(NewPersonal);
export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(WrappedNewBuilt);

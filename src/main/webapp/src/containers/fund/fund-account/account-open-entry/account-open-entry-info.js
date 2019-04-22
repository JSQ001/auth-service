import React from 'react';
import { connect } from 'dva';
import { Form, Input, Card, message, Select, DatePicker, Row, Col } from 'antd';
import moment from 'moment';
import Chooser from 'widget/chooser';
import accountOpenEntryService from './account-open-entry.service';

const { Option } = Select;
const FormItem = Form.Item;
/* eslint-disable */

class AccountOpenEntryInfo extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      isNew: true, // 是否为新建单据
      bankOptions: [], // 开户银行列表
      currencyList: [], // 币种
    };
  }

  componentDidMount() {
    this.getAccountBank();
    this.getCurrencyList();
  }

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

  render() {
    const {
      form: { getFieldDecorator },
      company,
      user,
    } = this.props;
    const { isNew, bankOptions, currencyList } = this.state;
    const formItemLayout = {
      labelCol: {
        span: 8,
      },
      wrapperCol: {
        span: 16,
      },
    };
    // const { applicationInformation, formData } = this.state;
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
                    initialValue: isNew ? '' : model.documentNumber,
                  })(<Input disabled />)}
                </FormItem>
              </Col>
              <Col span={6}>
                <FormItem label="申请公司" {...formItemLayout}>
                  {getFieldDecorator('company', {
                    rules: [{ required: true, message: this.$t('common.please.select') }],
                    initialValue: isNew
                      ? [{ id: company.id, name: company.name }]
                      : [{ id: model.companyId, name: model.companyName }],
                  })(
                    <Chooser
                      type="company"
                      labelKey="name"
                      valueKey="id"
                      showClear={false}
                      single
                      listExtraParams={{ setOfBooksId: company.setOfBooksId }}
                      disabled
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
                      : [{ departmentId: model.departmentId, path: model.departmentName }],
                  })(
                    <Chooser
                      type="department_document"
                      labelKey="path"
                      valueKey="departmentId"
                      single
                      listExtraParams={{ tenantId: user.tenantId }}
                      disabled
                    />
                  )}
                </FormItem>
              </Col>
              <Col span={6}>
                <FormItem {...formItemLayout} label="开户银行">
                  {getFieldDecorator('opneBank', {
                    initialValue: isNew ? [] : [{ key: model.openBank, label: model.openBankName }],
                  })(
                    <Select labelInValue placeholder="请选择" allowClear>
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
                      ? []
                      : [{ bankCode: model.branchBank, bankBranchName: model.branchBankName }],
                  })(<Input disabled />)}
                </FormItem>
              </Col>
              <Col span={6}>
                <FormItem label="币种" {...formItemLayout}>
                  {getFieldDecorator('currencyCode', {
                    rules: [{ required: true, message: this.$t('common.please.select') }],
                    initialValue: isNew ? company.baseCurrency : model.currencyCode,
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
                    initialValue: isNew ? moment() : moment(model.requisitionDate),
                  })(<DatePicker format="YYYY-MM-DD" />)}
                </FormItem>
              </Col>
              <Col span={6}>
                <FormItem label="申请人" {...formItemLayout}>
                  {getFieldDecorator('employeeName', {
                    rules: [{ required: true }],
                    initialValue: isNew ? user.userName : model.employeeName,
                  })(<Input disabled />)}
                </FormItem>
              </Col>
            </Row>
            <Row>
              <Col span={6}>
                <FormItem label="开户省" {...formItemLayout}>
                  {getFieldDecorator('openprovion', {
                    rules: [{ required: true }],
                    initialValue: isNew ? user.userName : model.employeeName,
                  })(<Input disabled />)}
                </FormItem>
              </Col>
              <Col span={6}>
                <FormItem label="开户市" {...formItemLayout}>
                  {getFieldDecorator('opencity', {
                    rules: [{ required: true }],
                    initialValue: isNew ? user.userName : model.employeeName,
                  })(<Input disabled />)}
                </FormItem>
              </Col>
              <Col span={8}>
                <FormItem label="备注" {...formItemLayout}>
                  {getFieldDecorator('remarks', {
                    initialValue: isNew ? '' : model.remarks,
                  })(<Input />)}
                </FormItem>
              </Col>
            </Row>
          </Form>
        </Card>
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

export default connect(mapStateToProps)(Form.create()(AccountOpenEntryInfo));

import React from 'react';
import {
  Card,
  Row,
  Col,
  Form,
  Input,
  Select,
  message,
  Checkbox,
  DatePicker,
  Button,
  Icon,
} from 'antd';
import { connect } from 'dva';
import moment from 'moment';
import Chooser from 'widget/chooser';
import accountService from './modify-account.service';

const FormItem = Form.Item;
const { Option } = Select;
// 申请信息组件
class modifyAccountEditInformation extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      accountPropertyList: [], // 账户性质值列表
      accountUseList: [], // 账户用途值
      accountDepositTypeList: [], // 存款类型值列表
      accountStatusList: [], // 账户状态值列表
      directFlagList: [], // 直联状态值列表
    };
  }

  componentDidMount() {
    this.getlistvalues();
  }

  /**
   * 获取所有值列表
   */
  getlistvalues = () => {
    // 获取账户性质
    this.getSystemValueList('ZJ_ACCOUNT_PORPERTY')
      .then(res => {
        this.setState({
          accountPropertyList: res.data.values,
        });
      })
      .catch(err => {
        message.error(err.message);
      });

    // 获取账户用途
    this.getSystemValueList('ZJ_ACCOUNT_USE')
      .then(res => {
        this.setState({
          accountUseList: res.data.values,
        });
      })
      .catch(err => {
        message.error(err.message);
      });

    // 获取存款类型
    this.getSystemValueList('ZJ_ACCOUNT_DEP_TYPE')
      .then(res => {
        this.setState({
          accountDepositTypeList: res.data.values,
        });
      })
      .catch(err => {
        message.error(err.message);
      });
    // 获取账户状态
    this.getSystemValueList('ZJ_ACCOUNT_STATUS')
      .then(res => {
        this.setState({
          accountStatusList: res.data.values,
        });
      })
      .catch(err => {
        message.error(err.message);
      });

    // 设置直联状态
    this.setState({
      directFlagList: [{ value: 1, name: '直联' }, { value: 0, name: '非直联' }],
    });
  };

  /**
   * 保存
   */
  saveApplicationInformation = e => {
    e.preventDefault();
    const { form, editApplicationInformation } = this.props;
    const { user } = this.props;
    form.validateFields((err, values) => {
      const saveData = {
        ...editApplicationInformation,
        accountDepositType: values.accountDepositType[0].key
          ? values.accountDepositType[0].key
          : '',
        accountDepositTypeName: values.accountDepositType[0].label
          ? values.accountDepositType[0].label
          : '',
        accountName: values.accountName ? values.accountName : '',
        accountNumber: editApplicationInformation.accountNumber,
        accountId: editApplicationInformation.id,
        companyId: user.companyId,
        employeeId: user.id,
        employeeName: user.userName,
        accountProperty: values.accountProperty[0].key ? values.accountProperty[0].key : '',
        accountPropertyName: values.accountProperty[0].label ? values.accountProperty[0].label : '',
        status: values.status[0].key ? values.status[0].key : '',
        statusDesc: values.status[0].label ? values.status[0].label : '',
        accountSubjectsCode: values.accountSubjectsCode ? values.accountSubjectsCode : '',
        accountUse: values.accountUse[0].key ? values.accountUse[0].key : '',
        accountUseName: values.accountUse[0].label ? values.accountUse[0].label : '',
        bankAddress: values.bankAddress ? values.bankAddress : '',
        customerManager: values.customerManager ? values.customerManager : '',
        directFlag: Number(values.directFlag[0].key) ? Number(values.directFlag[0].key) : '',
        finOfficialPrime: values.finOfficialPrime ? values.finOfficialPrime : '',
        interestRuleType: values.interestRuleType ? values.interestRuleType : '',
        openDate: moment(values.openDate).format() ? moment(values.openDate).format() : moment(),
        phone: values.phone ? values.phone : '',
        branchBankName: values.branchBankName ? values.branchBankName : '',
        branchBank: values.branchBank ? values.branchBank : '',
        ukeyFlag: values.ukeyFlag ? values.ukeyFlag : true,
        // regularAccountFlag: values.regularAccountFlag ? values.regularAccountFlag : '',
        ukey1ExpiryDate: moment(values.ukey1ExpiryDate).format()
          ? moment(values.ukey1ExpiryDate).format()
          : moment(),
        ukey1Num: values.ukey1Num ? values.ukey1Num : '',
        ukey1RegisterName: values.ukey1RegisterName[0].name ? values.ukey1RegisterName[0].name : '',
        ukey1UsingName: values.ukey1UsingName[0].name ? values.ukey1UsingName[0].name : '',
        ukey2ExpiryDate: moment(values.ukey2ExpiryDate).format()
          ? moment(values.ukey2ExpiryDate).format()
          : moment(),
        ukey2Num: values.ukey2Num ? values.ukey2Num : '',
        ukey2RegisterName: values.ukey2RegisterName[0].name ? values.ukey2RegisterName[0].name : '',
        ukey2UsingName: values.ukey2UsingName[0].name ? values.ukey2UsingName[0].name : '',
        ukey3ExpiryDate: moment(values.ukey3ExpiryDate).format()
          ? moment(values.ukey3ExpiryDate).format()
          : moment(),
        ukey3Num: values.ukey3Num ? values.ukey3Num : '',
        ukey3RegisterName: values.ukey3RegisterName[0].name ? values.ukey3RegisterName[0].name : '',
        ukey3UsingName: values.ukey3UsingName[0].name ? values.ukey3UsingName[0].name : '',
      };
      console.log(saveData);
      accountService
        .updateAccountModifyDetail(saveData)
        .then(res => {
          console.log(res);
          message.success('保存成功！');
        })
        .catch(error => {
          message.error(error.errorCode);
        });
    });
  };

  render() {
    const {
      form: { getFieldDecorator },
      editApplicationInformation,
      isReadOnly,
    } = this.props;
    const formItemLayout = {
      labelCol: {
        span: 8,
      },
      wrapperCol: {
        span: 16,
      },
    };
    const {
      accountPropertyList,
      accountUseList,
      accountDepositTypeList,
      directFlagList,
      accountStatusList,
    } = this.state;
    return (
      <div>
        <Card
          style={{
            boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)',
            marginRight: 15,
            marginLeft: 15,
            marginTop: 15,
          }}
        >
          <Form onSubmit={this.saveApplicationInformation}>
            <div
              style={{
                borderBottom: '1px solid rgb(236, 236, 236)',
              }}
            >
              <h3>基础信息:</h3>
            </div>
            <section style={{ paddingTop: '15px' }}>
              <Row>
                <Col span={6}>变更账号：{editApplicationInformation.accountNumber} </Col>
                <Col span={6}>单据编号：{editApplicationInformation.documentNumber} </Col>
                <Col span={6}>单据类型：账户变更 </Col>
                <Col span={6}>申请人：{editApplicationInformation.employeeName} </Col>
              </Row>
              <Row>
                <Col span={6}>开户银行：{editApplicationInformation.openBankName} </Col>
                <Col span={6}>明细段值：{editApplicationInformation.detailSegmentCode} </Col>
                <Col span={6}>{editApplicationInformation.detailSegmentCode} </Col>
                <Col span={6}>开户日期：{editApplicationInformation.openDate} </Col>
              </Row>
            </section>
            <div
              style={{
                borderBottom: '1px solid rgb(236, 236, 236)',
              }}
            >
              <h3>账户信息:</h3>
            </div>
            <section style={{ paddingTop: '15px' }}>
              <Row>
                <Col span={6}>
                  <FormItem label="户名" {...formItemLayout}>
                    {getFieldDecorator('accountName', {
                      rules: [{ required: true }],
                      initialValue: editApplicationInformation.accountName
                        ? editApplicationInformation.accountName
                        : '',
                    })(
                      <Input
                        placeholder="请维护"
                        setfieldsvalue={editApplicationInformation.accountName}
                        disabled={isReadOnly}
                      />
                    )}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="账户性质" {...formItemLayout}>
                    {getFieldDecorator('accountProperty', {
                      rules: [{ required: true }],
                      initialValue: editApplicationInformation.accountProperty
                        ? [
                            {
                              key: editApplicationInformation.accountProperty,
                              label: editApplicationInformation.accountPropertyName,
                            },
                          ]
                        : [{ key: '', value: '' }],
                    })(
                      <Select labelInValue placeholder="请选择" disabled={isReadOnly} allowClear>
                        {accountPropertyList.map(option => {
                          return <Option key={option.value}>{option.name}</Option>;
                        })}
                      </Select>
                    )}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="账户用途" {...formItemLayout}>
                    {getFieldDecorator('accountUse', {
                      rules: [{ required: true }],
                      initialValue: editApplicationInformation.accountUse
                        ? [
                            {
                              key: editApplicationInformation.accountUse,
                              label: editApplicationInformation.accountUseName,
                            },
                          ]
                        : [{ key: '', value: '' }],
                    })(
                      <Select labelInValue placeholder="请选择" disabled={isReadOnly} allowClear>
                        {accountUseList.map(option => {
                          return <Option key={option.value}>{option.name}</Option>;
                        })}
                      </Select>
                    )}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="存款类型" {...formItemLayout}>
                    {getFieldDecorator('accountDepositType', {
                      rules: [{ required: true }],
                      initialValue: editApplicationInformation.accountDepositType
                        ? [
                            {
                              key: editApplicationInformation.accountDepositType,
                              label: editApplicationInformation.accountDepositTypeName,
                            },
                          ]
                        : [{ key: '', label: '' }],
                    })(
                      <Select labelInValue placeholder="请选择" disabled={isReadOnly} allowClear>
                        {accountDepositTypeList.map(option => {
                          return <Option key={option.value}>{option.name}</Option>;
                        })}
                      </Select>
                    )}
                  </FormItem>
                </Col>
              </Row>
              <Row>
                <Col span={6}>
                  <FormItem label="预留印鉴" {...formItemLayout}>
                    {getFieldDecorator('finOfficialPrime', {
                      rules: [{ required: true }],
                      initialValue: editApplicationInformation.finOfficialPrime
                        ? editApplicationInformation.finOfficialPrime
                        : '',
                    })(
                      <Input
                        placeholder="请维护"
                        setfieldsvalue={editApplicationInformation.finOfficialPrime}
                        disabled={isReadOnly}
                      />
                    )}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="开户公司" {...formItemLayout}>
                    {getFieldDecorator('finOfficialPrime', {
                      rules: [{ required: true }],
                      initialValue: editApplicationInformation.companyName
                        ? editApplicationInformation.companyName
                        : '',
                    })(
                      <Input
                        placeholder="请维护"
                        setfieldsvalue={editApplicationInformation.finOfficialPrime}
                        disabled={isReadOnly}
                        allowClear
                      />
                    )}
                  </FormItem>
                </Col>

                <Col span={6}>
                  <FormItem label="计息规则" {...formItemLayout}>
                    {getFieldDecorator('interestRuleType', {
                      rules: [{ required: true }],
                      initialValue: editApplicationInformation.interestRuleType
                        ? editApplicationInformation.interestRuleType
                        : '',
                    })(<Input placeholder="请维护" disabled={isReadOnly} />)}
                  </FormItem>
                </Col>
              </Row>
              <Row>
                <Col span={12}>
                  <FormItem label="分支行信息" {...formItemLayout}>
                    {getFieldDecorator('branchBankName', {
                      rules: [{ required: true }],
                      initialValue: editApplicationInformation.branchBankName
                        ? editApplicationInformation.branchBankName
                        : '',
                    })(<Input placeholder="请维护" disabled={isReadOnly} />)}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="银行地址" {...formItemLayout}>
                    {getFieldDecorator('bankAddress', {
                      rules: [{ required: true }],
                      initialValue: editApplicationInformation.bankAddress
                        ? editApplicationInformation.bankAddress
                        : '',
                    })(<Input placeholder="请维护" disabled={isReadOnly} />)}
                  </FormItem>
                </Col>
              </Row>
              <Row>
                <Col span={6}>
                  <FormItem label="客户经理" {...formItemLayout}>
                    {getFieldDecorator('customerManager', {
                      rules: [{ required: true }],
                      initialValue: editApplicationInformation.customerManager
                        ? editApplicationInformation.customerManager
                        : '',
                    })(<Input placeholder="请维护" disabled={isReadOnly} />)}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="联系电话" {...formItemLayout}>
                    {getFieldDecorator('phone', {
                      rules: [{ required: true }],
                      initialValue: editApplicationInformation.phone
                        ? editApplicationInformation.phone
                        : '',
                    })(<Input placeholder="请维护" disabled={isReadOnly} />)}
                  </FormItem>
                </Col>
                <Col span={12}>
                  <FormItem label="核算科目" labelCol={{ span: 4 }} wrapperCol={{ span: 16 }}>
                    {getFieldDecorator('accountSubjectsCode', {
                      rules: [{ required: true }],
                      initialValue: editApplicationInformation.accountSubjectsCode
                        ? editApplicationInformation.accountSubjectsCode
                        : '',
                    })(<Input placeholder="请维护" disabled={isReadOnly} />)}
                  </FormItem>
                </Col>
              </Row>
              <Row>
                <Col span={6}>
                  <FormItem label="直联状态" {...formItemLayout}>
                    {getFieldDecorator('directFlag', {
                      rules: [{ required: true }],
                      initialValue: editApplicationInformation.directFlag
                        ? [{ key: 1, label: '直联' }]
                        : [{ key: 0, label: '非直联' }],
                    })(
                      <Select labelInValue placeholder="请选择" disabled={isReadOnly} allowClear>
                        {directFlagList.map(option => {
                          return <Option key={option.value}>{option.name}</Option>;
                        })}
                      </Select>
                    )}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="账户状态" {...formItemLayout}>
                    {getFieldDecorator('status', {
                      rules: [{ required: true }],
                      initialValue: editApplicationInformation.status
                        ? [
                            {
                              key: editApplicationInformation.status,
                              label: editApplicationInformation.statusDesc,
                            },
                          ]
                        : [{ key: '', value: '' }],
                    })(
                      <Select labelInValue placeholder="请选择" disabled={isReadOnly} allowClear>
                        {accountStatusList.map(option => {
                          return <Option key={option.value}>{option.name}</Option>;
                        })}
                      </Select>
                    )}
                  </FormItem>
                </Col>
                <Col span={4}>
                  <FormItem {...formItemLayout}>
                    {getFieldDecorator('ukeyFlag', {})(
                      <Checkbox
                        onChange={this.ukeyFlagChange}
                        checked={editApplicationInformation.ukeyFlag}
                        style={{ marginLeft: '20px' }}
                        disabled={isReadOnly}
                      >
                        网银key
                      </Checkbox>
                    )}
                  </FormItem>
                </Col>
              </Row>
              <Row>
                <Col span={6}>
                  <FormItem label="UKEY编号1" {...formItemLayout}>
                    {getFieldDecorator('ukey1Num', {
                      rules: [{ required: true }],
                      initialValue: editApplicationInformation.ukey1Num
                        ? editApplicationInformation.ukey1Num
                        : '',
                    })(<Input placeholder="请维护" disabled={isReadOnly} />)}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="登记员工" {...formItemLayout}>
                    {getFieldDecorator('ukey1RegisterName', {
                      rules: [{ required: true }],
                      initialValue: editApplicationInformation.ukey1RegisterName
                        ? [
                            {
                              id: editApplicationInformation.ukey1RegisterName,
                              name: editApplicationInformation.ukey1RegisterName,
                            },
                          ]
                        : [{ id: '', name: '' }],
                    })(
                      <Chooser
                        type="select_employee"
                        valueKey="id"
                        labelKey="name"
                        single
                        disabled={isReadOnly}
                      />
                    )}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="使用员工" {...formItemLayout}>
                    {getFieldDecorator('ukey1UsingName', {
                      rules: [{ required: true }],
                      initialValue: editApplicationInformation.ukey1UsingName
                        ? [
                            {
                              id: editApplicationInformation.ukey1UsingName,
                              name: editApplicationInformation.ukey1UsingName,
                            },
                          ]
                        : [{ id: '', name: '' }],
                    })(
                      <Chooser
                        type="select_employee"
                        valueKey="id"
                        labelKey="name"
                        single
                        disabled={isReadOnly}
                      />
                    )}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="证书到期" {...formItemLayout}>
                    {getFieldDecorator('ukey1ExpiryDate', {
                      rules: [{ required: true }],
                      initialValue: editApplicationInformation.ukey1ExpiryDate
                        ? moment(editApplicationInformation.ukey1ExpiryDate)
                        : moment(),
                    })(<DatePicker format="YYYY-MM-DD" disabled={isReadOnly} />)}
                  </FormItem>
                </Col>
              </Row>
              <Row>
                <Col span={6}>
                  <FormItem label="UKEY编号2" {...formItemLayout}>
                    {getFieldDecorator('ukey2Num', {
                      rules: [{ required: true }],
                      initialValue: editApplicationInformation.ukey2Num
                        ? editApplicationInformation.ukey2Num
                        : '',
                    })(<Input placeholder="请维护" disabled={isReadOnly} />)}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="登记员工" {...formItemLayout}>
                    {getFieldDecorator('ukey2RegisterName', {
                      rules: [{ required: true }],
                      initialValue: editApplicationInformation.ukey2RegisterName
                        ? [
                            {
                              id: editApplicationInformation.ukey2RegisterName,
                              name: editApplicationInformation.ukey2RegisterName,
                            },
                          ]
                        : [{ id: '', name: '' }],
                    })(
                      <Chooser
                        type="select_employee"
                        valueKey="id"
                        labelKey="name"
                        single
                        disabled={isReadOnly}
                      />
                    )}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="使用员工" {...formItemLayout}>
                    {getFieldDecorator('ukey2UsingName', {
                      rules: [{ required: true }],
                      initialValue: editApplicationInformation.ukey2UsingName
                        ? [
                            {
                              id: editApplicationInformation.ukey2UsingName,
                              name: editApplicationInformation.ukey2UsingName,
                            },
                          ]
                        : [{ id: '', name: '' }],
                    })(
                      <Chooser
                        type="select_employee"
                        valueKey="id"
                        labelKey="name"
                        single
                        disabled={isReadOnly}
                      />
                    )}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="证书到期" {...formItemLayout}>
                    {getFieldDecorator('ukey2ExpiryDate', {
                      rules: [{ required: true }],
                      initialValue: editApplicationInformation.ukey2ExpiryDate
                        ? moment(editApplicationInformation.ukey2ExpiryDate)
                        : moment(),
                    })(<DatePicker format="YYYY-MM-DD" disabled={isReadOnly} />)}
                  </FormItem>
                </Col>
              </Row>
              <Row>
                <Col span={6}>
                  <FormItem label="UKEY编号3" {...formItemLayout}>
                    {getFieldDecorator('ukey3Num', {
                      rules: [{ required: true }],
                      initialValue: editApplicationInformation.ukey3Num
                        ? editApplicationInformation.ukey3Num
                        : '',
                    })(<Input placeholder="请维护" disabled={isReadOnly} />)}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="登记员工" {...formItemLayout}>
                    {getFieldDecorator('ukey3RegisterName', {
                      rules: [{ required: true }],
                      initialValue: editApplicationInformation.ukey3RegisterName
                        ? [
                            {
                              id: editApplicationInformation.ukey3RegisterName,
                              name: editApplicationInformation.ukey3RegisterName,
                            },
                          ]
                        : [{ id: '', name: '' }],
                    })(
                      <Chooser
                        type="select_employee"
                        valueKey="id"
                        labelKey="name"
                        single
                        disabled={isReadOnly}
                      />
                    )}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="使用员工" {...formItemLayout}>
                    {getFieldDecorator('ukey3UsingName', {
                      rules: [{ required: true }],
                      initialValue: editApplicationInformation.ukey3UsingName
                        ? [
                            {
                              id: editApplicationInformation.ukey3UsingName,
                              name: editApplicationInformation.ukey3UsingName,
                            },
                          ]
                        : [{ id: '', name: '' }],
                    })(
                      <Chooser
                        type="select_employee"
                        valueKey="id"
                        labelKey="name"
                        single
                        disabled={isReadOnly}
                      />
                    )}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="证书到期" {...formItemLayout}>
                    {getFieldDecorator('ukey3ExpiryDate', {
                      rules: [{ required: true }],
                      initialValue: editApplicationInformation.ukey3ExpiryDate
                        ? moment(editApplicationInformation.ukey3ExpiryDate)
                        : moment(),
                    })(<DatePicker format="YYYY-MM-DD" disabled={isReadOnly} />)}
                  </FormItem>
                </Col>
              </Row>
              <Row>
                <Col span={6}>
                  <FormItem label="存款期限" {...formItemLayout}>
                    {getFieldDecorator('depositTerm', {
                      rules: [{ required: true }],
                      initialValue: editApplicationInformation.depositTerm
                        ? moment(editApplicationInformation.depositTerm)
                        : moment(),
                    })(<Input placeholder="请维护" disabled={isReadOnly} />)}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="提醒时间" {...formItemLayout}>
                    {getFieldDecorator('reminderDate', {
                      rules: [{ required: true }],
                      initialValue: editApplicationInformation.reminderDate
                        ? moment(editApplicationInformation.reminderDate)
                        : moment(),
                    })(<DatePicker format="YYYY-MM-DD" disabled={isReadOnly} />)}
                  </FormItem>
                </Col>
              </Row>
            </section>
            <section style={{ paddingTop: '15px' }}>
              <Row>
                <Col span={12}>
                  <FormItem label="说明" labelCol={{ span: 4 }} wrapperCol={{ span: 16 }}>
                    {getFieldDecorator('accountSubjectsCode', {
                      rules: [{ required: true }],
                      initialValue: editApplicationInformation.accountSubjectsCode
                        ? editApplicationInformation.accountSubjectsCode
                        : '',
                    })(<Input placeholder="请维护" disabled={isReadOnly} />)}
                  </FormItem>
                </Col>
              </Row>
            </section>
            {/* <FormItem
              label="附件信息"
              labelCol={{ span: 1 }}
              style={{ width: '300px', height: '100px', textAlign: 'center', margin: ' 0 auto' }}
            >
              {getFieldDecorator('attachmentOID')(
                <Upload
                  attachmentType="CONTRACT"
                  uploadUrl={`${config.baseUrl}/api/upload/static/attachment`}
                  fileNum={9}
                  uploadHandle={this.handleUpload}
                  defaultFileList={fileList}
                  defaultOIDs={isNew ? [] : model.attachmentOidList}
                />
            )}
          </FormItem> */}
            <div style={{ textAlign: 'right' }}>
              <a
                style={{ fontSize: '14px', paddingBottom: '20px', float: 'left' }}
                onClick={this.onClickBack}
              >
                <Icon type="rollback" style={{ marginRight: '5px' }} />返回
              </a>
              {/* <Button style={{ margin: '0 20px', float: 'right' }} onClick={this.onClickBack}>
                返回
              </Button> */}
              {!isReadOnly && (
                <div style={{ float: 'right' }}>
                  <Button
                    type="primary"
                    htmlType="submit"
                    style={{ margin: '0 20px' }}
                    onClick={this.saveApplicationInformation}
                  >
                    保存
                  </Button>
                  <Button type="primary" style={{ margin: '0 20px' }}>
                    提交
                  </Button>
                </div>
              )}
            </div>
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

export default connect(mapStateToProps)(Form.create()(modifyAccountEditInformation));

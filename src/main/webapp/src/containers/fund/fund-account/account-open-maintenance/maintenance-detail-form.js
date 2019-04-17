import React from 'react';
import {
  Form,
  Input,
  Row,
  Col,
  Card,
  Button,
  Select,
  message,
  DatePicker,
  Checkbox,
  Icon,
} from 'antd';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import Chooser from 'widget/chooser';
import moment from 'moment';

import maintenanceService from './account-open-maintenance.service';

const FormItem = Form.Item;
const { Option } = Select;
class maintenanceDetailFrom extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      accountPropertyList: [], // 账户性质值列表
      accountUseList: [], // 账户用途值
      accountDepositTypeList: [], // 存款类型值列表
      depositDurationList: [], // 获取存款期限值列表
      directFlagList: [], // 直联状态值列表
      versionNumber: null, // 保存后获得的versionNumber
      isReadOnly: false, // 是否只读
      saveSuccess: false, // 保存成功的标准
    };
  }

  componentDidMount() {
    this.getlistvalues();
  }

  /**
   * 获取是否只读
   */
  getIsReadOnly = value => {
    if (value === 'ZJ_APPROVED' || value === 'ZJ_PENGDING') {
      return true;
    } else {
      return false;
    }
  };

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
        message.error(err.response.data.message);
      });

    // 获取账户用途
    this.getSystemValueList('ZJ_ACCOUNT_USE')
      .then(res => {
        this.setState({
          accountUseList: res.data.values,
        });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });

    // 获取存款类型
    this.getSystemValueList('ZJ_ACCOUNT_DEP_TYPE')
      .then(res => {
        this.setState({
          accountDepositTypeList: res.data.values,
        });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });

    // 获取存款期限
    this.getSystemValueList('ZJ_DEPOSIT_DURATION')
      .then(res => {
        this.setState({
          depositDurationList: res.data.values,
        });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });

    // 设置直联状态
    this.setState({
      directFlagList: [{ value: 1, name: '直联' }, { value: 0, name: '非直联' }],
    });
  };

  /**
   * regularAccountFlag变换
   */

  regularAccountFlagChange = () => {
    const { formData, form } = this.props;
    formData.regularAccountFlag = !formData.regularAccountFlag;
    form.setFields({
      reminderDate: {
        errors: '',
      },
      depositTerm: {
        errors: '',
      },
    });
  };

  /**
   * ukeyFlag变换
   */
  ukeyFlagChange = () => {
    const { formData, form } = this.props;
    formData.ukeyFlag = !formData.ukeyFlag;
    form.setFields({
      ukey1Num: {
        errors: '',
      },
      ukey1RegisterName: {
        errors: '',
      },
      ukey1UsingName: {
        errors: '',
      },
      ukey1ExpiryDate: {
        errors: '',
      },
      ukey2Num: {
        errors: '',
      },
      ukey2RegisterName: {
        errors: '',
      },
      ukey2UsingName: {
        errors: '',
      },
      ukey2ExpiryDate: {
        errors: '',
      },
      ukey3Num: {
        errors: '',
      },
      ukey3RegisterName: {
        errors: '',
      },
      ukey3UsingName: {
        errors: '',
      },
      ukey3ExpiryDate: {
        errors: '',
      },
    });
  };

  /**
   * 判断是否为数组
   */
  isArray = arr => {
    return Array.isArray(arr) && arr.length !== 0;
  };

  /**
   * 保存
   */
  saveFormData = async () => {
    const { form, formData } = this.props;
    const { versionNumber } = this.state;
    form.validateFields((err, values) => {
      if (!err) {
        const acountUkeyInfo = {
          ...formData.acountUkeyInfo,
          ukey1ExpiryDate: values.ukey1ExpiryDate ? moment(values.ukey1ExpiryDate).format() : null,
          ukey1Num: values.ukey1Num,
          ukey1RegisterName: values.ukey1RegisterName[0] ? values.ukey1RegisterName[0].name : null,
          ukey1UsingName: values.ukey1UsingName[0] ? values.ukey1UsingName[0].name : null,
          ukey2ExpiryDate: values.ukey2ExpiryDate ? moment(values.ukey2ExpiryDate).format() : null,
          ukey2Num: values.ukey2Num,
          ukey2RegisterName: values.ukey2RegisterName[0] ? values.ukey2RegisterName[0].name : null,
          ukey2UsingName: values.ukey2RegisterName[0] ? values.ukey2UsingName[0].name : null,
          ukey3ExpiryDate: values.ukey3ExpiryDate ? moment(values.ukey3ExpiryDate).format() : null,
          ukey3Num: values.ukey3Num,
          ukey3RegisterName: values.ukey3RegisterName[0] ? values.ukey3RegisterName[0].name : null,
          ukey3UsingName: values.ukey3UsingName[0] ? values.ukey3UsingName[0].name : null,
        };
        const saveData = {
          ...formData,
          accountDepositType: this.isArray(values.accountDepositType)
            ? values.accountDepositType[0].key
            : values.accountDepositType.key,
          accountDepositTypeName: this.isArray(values.accountDepositType)
            ? values.accountDepositType[0].label
            : values.accountDepositType.label,
          accountName: values.accountName,
          accountNumber: values.accountNumber,
          accountProperty: this.isArray(values.accountProperty)
            ? values.accountProperty[0].key
            : values.accountProperty.key,
          accountPropertyName: this.isArray(values.accountProperty)
            ? values.accountProperty[0].label
            : values.accountProperty.label,
          accountSubjectsCode: values.accountSubjectsCode,
          accountUse: this.isArray(values.accountUse)
            ? values.accountUse[0].key
            : values.accountUse.key,
          accountUseName: this.isArray(values.accountUse)
            ? values.accountUse[0].label
            : values.accountUse.label,
          bankAddress: values.bankAddress,
          customerManager: values.customerManager,
          detailSegmentCode: values.detailSegmentCode,
          directFlag: this.isArray(values.directFlag)
            ? Number(values.directFlag[0].key)
            : values.directFlag.key,
          finOfficialPrime: values.finOfficialPrime,
          interestRuleType: values.interestRuleType,
          openDate: moment(values.openDate).format(),
          depositTerm: this.isArray(values.depositTerm)
            ? values.depositTerm[0].key
            : values.depositTerm.key,
          reminderDate: values.reminderDate,
          phone: values.phone,
          ukeyFlag: values.ukeyFlag,
          regularAccountFlag: values.regularAccountFlag,
          acountUkeyInfo,
        };
        if (versionNumber) {
          saveData.versionNumber = versionNumber;
          if (saveData.acountUkeyInfo.versionNumber) {
            delete saveData.acountUkeyInfo.versionNumber; // acountUkeyInfo不需要versionNumber
          }
        }
        maintenanceService
          .updateAccountOpenMaintenanceDetail(saveData)
          .then(res => {
            if (res.status === 200) {
              this.setState({
                versionNumber: res.data.versionNumber,
                saveSuccess: true,
              });
              message.success('保存成功！');
            }
          })
          .catch(error => {
            message.error(error.response.data.message);
          });
      }
    });
  };

  submitDetail = async () => {
    const { formData } = this.props;
    const { saveSuccess } = this.state;
    if (saveSuccess) {
      await maintenanceService
        .submit(formData.id)
        .then(res => {
          if (res.status === 200) {
            message.success('提交成功！');
            this.onClickBack();
          }
        })
        .catch(error => {
          message.error(error.response.data.message);
        });
    } else {
      message.error('先保存再提交！');
    }
  };

  /**
   * 返回
   */
  onClickBack = () => {
    const { dispatch, from } = this.props;
    if (from === 'openMaintenanceList') {
      dispatch(
        routerRedux.push({
          pathname: `/account-manage/account-open-maintenance/account-open-maintenance-list`,
        })
      );
    } else {
      dispatch(
        routerRedux.push({
          pathname: `/account-manage/account-show/account-show`,
        })
      );
    }
  };

  render() {
    const {
      form: { getFieldDecorator },
      formData,
      accountShowReadOnly,
    } = this.props;
    let acountUkeyInfoCopy = {};
    const { acountUkeyInfo } = formData;
    if (acountUkeyInfo) {
      acountUkeyInfoCopy = acountUkeyInfo;
    }
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
      depositDurationList,
      directFlagList,
    } = this.state;
    let { isReadOnly } = this.state;
    if (formData.approveStatus) {
      isReadOnly = accountShowReadOnly || this.getIsReadOnly(formData.maintainApproveStatus);
    }
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
          <Form onSubmit={this.saveFormData}>
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
                  <FormItem label="账号" {...formItemLayout}>
                    {getFieldDecorator('accountNumber', {
                      rules: [{ required: true }],
                      initialValue: formData.accountNumber ? formData.accountNumber : '',
                    })(
                      <Input
                        placeholder="请维护"
                        setfieldsvalue={formData.accountNumber}
                        disabled={isReadOnly}
                      />
                    )}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="户名" {...formItemLayout}>
                    {getFieldDecorator('accountName', {
                      rules: [{ required: true }],
                      initialValue: formData.accountName ? formData.accountName : '',
                    })(
                      <Input
                        placeholder="请维护"
                        setfieldsvalue={formData.accountName}
                        disabled={isReadOnly}
                      />
                    )}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="账户性质" {...formItemLayout}>
                    {getFieldDecorator('accountProperty', {
                      rules: [{ required: true }],
                      initialValue: formData.accountProperty
                        ? [{ key: formData.accountProperty, label: formData.accountPropertyName }]
                        : [],
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
                      initialValue: formData.accountUse
                        ? [{ key: formData.accountUse, label: formData.accountUseName }]
                        : [],
                    })(
                      <Select labelInValue placeholder="请选择" disabled={isReadOnly} allowClear>
                        {accountUseList.map(option => {
                          return <Option key={option.value}>{option.name}</Option>;
                        })}
                      </Select>
                    )}
                  </FormItem>
                </Col>
              </Row>
              <Row>
                <Col span={6}>
                  <FormItem label="存款类型" {...formItemLayout}>
                    {getFieldDecorator('accountDepositType', {
                      rules: [{ required: true }],
                      initialValue: formData.accountDepositType
                        ? [
                            {
                              key: formData.accountDepositType,
                              label: formData.accountDepositTypeName,
                            },
                          ]
                        : [],
                    })(
                      <Select labelInValue placeholder="请选择" disabled={isReadOnly} allowClear>
                        {accountDepositTypeList.map(option => {
                          return <Option key={option.value}>{option.name}</Option>;
                        })}
                      </Select>
                    )}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="预留印鉴" {...formItemLayout}>
                    {getFieldDecorator('finOfficialPrime', {
                      rules: [{ required: true }],
                      initialValue: formData.finOfficialPrime ? formData.finOfficialPrime : '',
                    })(
                      <Input
                        placeholder="请维护"
                        setfieldsvalue={formData.finOfficialPrime}
                        disabled={isReadOnly}
                      />
                    )}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="直联状态" {...formItemLayout}>
                    {getFieldDecorator('directFlag', {
                      rules: [{ required: true }],
                      initialValue: formData.directFlag
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
                  <FormItem label="开户日期" {...formItemLayout}>
                    {getFieldDecorator('openDate', {
                      rules: [{ required: true }],
                      initialValue: formData.openDate ? moment(formData.openDate) : moment(),
                    })(<DatePicker format="YYYY-MM-DD" disabled={isReadOnly} />)}
                  </FormItem>
                </Col>
              </Row>
              <Row>
                <Col span={6}>
                  <FormItem label="计息规则" {...formItemLayout}>
                    {getFieldDecorator('interestRuleType', {
                      rules: [{ required: true }],
                      initialValue: formData.interestRuleType ? formData.interestRuleType : '',
                    })(<Input placeholder="请维护" disabled={isReadOnly} />)}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="银行地址" {...formItemLayout}>
                    {getFieldDecorator('bankAddress', {
                      rules: [{ required: true }],
                      initialValue: formData.bankAddress ? formData.bankAddress : '',
                    })(<Input placeholder="请维护" disabled={isReadOnly} />)}
                  </FormItem>
                </Col>
              </Row>
              <Row>
                <Col span={6}>
                  <FormItem label="客户经理" {...formItemLayout}>
                    {getFieldDecorator('customerManager', {
                      rules: [{ required: true }],
                      initialValue: formData.customerManager ? formData.customerManager : '',
                    })(<Input placeholder="请维护" disabled={isReadOnly} />)}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="联系电话" {...formItemLayout}>
                    {getFieldDecorator('phone', {
                      rules: [{ required: true }],
                      initialValue: formData.phone ? formData.phone : '',
                    })(<Input placeholder="请维护" disabled={isReadOnly} />)}
                  </FormItem>
                </Col>
                <Col span={5} offset={1}>
                  <FormItem {...formItemLayout}>
                    {getFieldDecorator('ukeyFlag', {})(
                      <Checkbox
                        onChange={this.ukeyFlagChange}
                        checked={formData.ukeyFlag}
                        style={{ marginLeft: '20px' }}
                        disabled={isReadOnly}
                      >
                        网银key
                      </Checkbox>
                    )}
                  </FormItem>
                </Col>
                <Col span={5} offset={1}>
                  <FormItem {...formItemLayout}>
                    {getFieldDecorator('regularAccountFlag', {})(
                      <Checkbox
                        onChange={this.regularAccountFlagChange}
                        checked={formData.regularAccountFlag}
                        disabled={isReadOnly}
                      >
                        定期账户提醒
                      </Checkbox>
                    )}
                  </FormItem>
                </Col>
              </Row>
            </section>
            <div
              style={{
                borderBottom: '1px solid rgb(236, 236, 236)',
              }}
            >
              <h3>核算信息:</h3>
            </div>
            <section style={{ paddingTop: '15px' }}>
              <Row>
                <Col span={12}>
                  <FormItem label="核算科目" labelCol={{ span: 4 }} wrapperCol={{ span: 16 }}>
                    {getFieldDecorator('accountSubjectsCode', {
                      rules: [{ required: true }],
                      initialValue: formData.accountSubjectsCode
                        ? formData.accountSubjectsCode
                        : '',
                    })(<Input placeholder="请维护" disabled={isReadOnly} />)}
                  </FormItem>
                </Col>
              </Row>
              <Row>
                <Col span={12}>
                  <FormItem label="明细段值" labelCol={{ span: 4 }} wrapperCol={{ span: 16 }}>
                    {getFieldDecorator('detailSegmentCode', {
                      rules: [{ required: true }],
                      initialValue: formData.detailSegmentCode ? formData.detailSegmentCode : '',
                    })(<Input placeholder="请维护" disabled={isReadOnly} />)}
                  </FormItem>
                </Col>
              </Row>
            </section>
            <div
              style={{
                borderBottom: '1px solid rgb(236, 236, 236)',
              }}
            >
              <h3>辅助功能:</h3>
            </div>
            <section style={{ paddingTop: '15px' }}>
              <Row>
                <Col span={6}>
                  <FormItem label="UKEY编号1" {...formItemLayout}>
                    {getFieldDecorator('ukey1Num', {
                      rules: [{ required: formData.ukeyFlag }],
                      initialValue: acountUkeyInfoCopy.ukey1Num ? acountUkeyInfoCopy.ukey1Num : '',
                    })(<Input placeholder="请维护" disabled={isReadOnly || !formData.ukeyFlag} />)}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="登记员工" {...formItemLayout}>
                    {getFieldDecorator('ukey1RegisterName', {
                      rules: [{ required: formData.ukeyFlag }],
                      initialValue: acountUkeyInfoCopy.ukey1RegisterName
                        ? [
                            {
                              id: acountUkeyInfoCopy.ukey1RegisterName,
                              name: acountUkeyInfoCopy.ukey1RegisterName,
                            },
                          ]
                        : [],
                    })(
                      <Chooser
                        type="select_employee"
                        valueKey="id"
                        labelKey="name"
                        single
                        disabled={isReadOnly || !formData.ukeyFlag}
                      />
                    )}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="使用员工" {...formItemLayout}>
                    {getFieldDecorator('ukey1UsingName', {
                      rules: [{ required: formData.ukeyFlag }],
                      initialValue: acountUkeyInfoCopy.ukey1UsingName
                        ? [
                            {
                              id: acountUkeyInfoCopy.ukey1UsingName,
                              name: acountUkeyInfoCopy.ukey1UsingName,
                            },
                          ]
                        : [],
                    })(
                      <Chooser
                        type="select_employee"
                        valueKey="id"
                        labelKey="name"
                        single
                        disabled={isReadOnly || !formData.ukeyFlag}
                      />
                    )}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="证书到期" {...formItemLayout}>
                    {getFieldDecorator('ukey1ExpiryDate', {
                      rules: [{ required: formData.ukeyFlag }],
                      initialValue: acountUkeyInfoCopy.ukey1ExpiryDate
                        ? moment(acountUkeyInfoCopy.ukey1ExpiryDate)
                        : '',
                    })(
                      <DatePicker format="YYYY-MM-DD" disabled={isReadOnly || !formData.ukeyFlag} />
                    )}
                  </FormItem>
                </Col>
              </Row>
              <Row>
                <Col span={6}>
                  <FormItem label="UKEY编号2" {...formItemLayout}>
                    {getFieldDecorator('ukey2Num', {
                      rules: [{ required: formData.ukeyFlag }],
                      initialValue: acountUkeyInfoCopy.ukey2Num ? acountUkeyInfoCopy.ukey2Num : '',
                    })(<Input placeholder="请维护" disabled={isReadOnly || !formData.ukeyFlag} />)}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="登记员工" {...formItemLayout}>
                    {getFieldDecorator('ukey2RegisterName', {
                      rules: [{ required: formData.ukeyFlag }],
                      initialValue: acountUkeyInfoCopy.ukey2RegisterName
                        ? [
                            {
                              id: acountUkeyInfoCopy.ukey2RegisterName,
                              name: acountUkeyInfoCopy.ukey2RegisterName,
                            },
                          ]
                        : [],
                    })(
                      <Chooser
                        type="select_employee"
                        valueKey="id"
                        labelKey="name"
                        single
                        disabled={isReadOnly || !formData.ukeyFlag}
                      />
                    )}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="使用员工" {...formItemLayout}>
                    {getFieldDecorator('ukey2UsingName', {
                      rules: [{ required: formData.ukeyFlag }],
                      initialValue: acountUkeyInfoCopy.ukey2UsingName
                        ? [
                            {
                              id: acountUkeyInfoCopy.ukey2UsingName,
                              name: acountUkeyInfoCopy.ukey2UsingName,
                            },
                          ]
                        : [],
                    })(
                      <Chooser
                        type="select_employee"
                        valueKey="id"
                        labelKey="name"
                        single
                        disabled={isReadOnly || !formData.ukeyFlag}
                      />
                    )}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="证书到期" {...formItemLayout}>
                    {getFieldDecorator('ukey2ExpiryDate', {
                      rules: [{ required: formData.ukeyFlag }],
                      initialValue: acountUkeyInfoCopy.ukey2ExpiryDate
                        ? moment(acountUkeyInfoCopy.ukey2ExpiryDate)
                        : '',
                    })(
                      <DatePicker format="YYYY-MM-DD" disabled={isReadOnly || !formData.ukeyFlag} />
                    )}
                  </FormItem>
                </Col>
              </Row>
              <Row>
                <Col span={6}>
                  <FormItem label="UKEY编号3" {...formItemLayout}>
                    {getFieldDecorator('ukey3Num', {
                      rules: [{ required: formData.ukeyFlag }],
                      initialValue: acountUkeyInfoCopy.ukey3Num ? acountUkeyInfoCopy.ukey3Num : '',
                    })(<Input placeholder="请维护" disabled={isReadOnly || !formData.ukeyFlag} />)}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="登记员工" {...formItemLayout}>
                    {getFieldDecorator('ukey3RegisterName', {
                      rules: [{ required: formData.ukeyFlag }],
                      initialValue: acountUkeyInfoCopy.ukey3RegisterName
                        ? [
                            {
                              id: acountUkeyInfoCopy.ukey3RegisterName,
                              name: acountUkeyInfoCopy.ukey3RegisterName,
                            },
                          ]
                        : [],
                    })(
                      <Chooser
                        type="select_employee"
                        valueKey="id"
                        labelKey="name"
                        single
                        disabled={isReadOnly || !formData.ukeyFlag}
                      />
                    )}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="使用员工" {...formItemLayout}>
                    {getFieldDecorator('ukey3UsingName', {
                      rules: [{ required: formData.ukeyFlag }],
                      initialValue: acountUkeyInfoCopy.ukey3UsingName
                        ? [
                            {
                              id: acountUkeyInfoCopy.ukey3UsingName,
                              name: acountUkeyInfoCopy.ukey3UsingName,
                            },
                          ]
                        : [],
                    })(
                      <Chooser
                        type="select_employee"
                        valueKey="id"
                        labelKey="name"
                        single
                        disabled={isReadOnly || !formData.ukeyFlag}
                      />
                    )}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="证书到期" {...formItemLayout}>
                    {getFieldDecorator('ukey3ExpiryDate', {
                      rules: [{ required: formData.ukeyFlag }],
                      initialValue: acountUkeyInfoCopy.ukey3ExpiryDate
                        ? moment(acountUkeyInfoCopy.ukey3ExpiryDate)
                        : '',
                    })(
                      <DatePicker format="YYYY-MM-DD" disabled={isReadOnly || !formData.ukeyFlag} />
                    )}
                  </FormItem>
                </Col>
              </Row>
              <Row>
                <Col span={6}>
                  <FormItem label="存款期限" {...formItemLayout}>
                    {getFieldDecorator('depositTerm', {
                      rules: [{ required: formData.regularAccountFlag }],
                      initialValue: formData.depositTerm
                        ? [
                            {
                              key: formData.depositTerm,
                              label: formData.depositTermName,
                            },
                          ]
                        : [],
                    })(
                      <Select
                        labelInValue
                        placeholder="请选择"
                        disabled={isReadOnly || !formData.regularAccountFlag}
                      >
                        {depositDurationList.map(option => {
                          return <Option key={option.value}>{option.name}</Option>;
                        })}
                      </Select>
                    )}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="提醒日期" {...formItemLayout}>
                    {getFieldDecorator('reminderDate', {
                      rules: [{ required: formData.regularAccountFlag }],
                      initialValue: formData.reminderDate ? moment(formData.reminderDate) : '',
                    })(
                      <DatePicker
                        format="YYYY-MM-DD"
                        disabled={isReadOnly || !formData.regularAccountFlag}
                      />
                    )}
                  </FormItem>
                </Col>
              </Row>
            </section>
            <div style={{ textAlign: 'right' }}>
              <a
                style={{ fontSize: '14px', paddingBottom: '20px', float: 'left' }}
                onClick={this.onClickBack}
              >
                <Icon type="rollback" style={{ marginRight: '5px' }} />返回
              </a>
              {!isReadOnly && (
                <div style={{ float: 'right' }}>
                  <Button type="primary" style={{ margin: '0 20px' }} onClick={this.saveFormData}>
                    保存
                  </Button>
                  <Button type="primary" onClick={this.submitDetail} style={{ margin: '0 20px' }}>
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

function map(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
  };
}

export default connect(map)(Form.create()(maintenanceDetailFrom));

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
import { routerRedux } from 'dva/router';
import moment from 'moment';
import Chooser from 'widget/chooser';
import Upload from 'widget/upload-button';
import config from 'config';
import accountService from './modify-account.service';

const FormItem = Form.Item;
const { Option } = Select;
// 申请信息组件
class modifyAccountAddInformation extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      uploadOIDs: [], // 附件oid
      isCan: false, // 定期存款是否可以修改
      backMessage: [], // 保存后返回的信息
      accountPropertyList: [], // 账户性质值列表
      accountUseList: [], // 账户用途值
      accountDepositTypeList: [], // 存款类型值列表
      accountStatusList: [], // 账户状态值列表
      accountStatusList1: [], // 账户状态值列表
      directFlagList: [], // 直联状态值列表
      submitFlag: false, // 是否提交的标志
      depositDurationList: [], // 存款期限值列表
    };
  }

  componentDidMount() {
    this.getlistvalues();
  }

  /**
   * 判断是否有值
   */
  isundenfind = arr => {
    return arr.length !== 0;
  };

  /**
   * 判断是否有值
   */
  isArray = arr => {
    return Array.isArray(arr) && arr.length !== 0;
  };

  /**
   * 判断是否为空
   */
  isNull = arr => {
    return arr !== '';
  };

  /**
   * 获取所有值列表
   */
  getlistvalues = () => {
    const { accountStatusList1 } = this.state;
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
        for (const i of res.data.values) {
          if (i.value === 'ZJ_FROZEN' || i.value === 'ZJ_CLOSED' || i.value === 'ZJ_NORMAL') {
            accountStatusList1.push(i);
          }
        }
        this.setState({
          accountStatusList: accountStatusList1,
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
   * 上传附件
   */
  handleUpload = OIDs => {
    const { params } = this.props;
    const { isTrue } = this.state;
    if (isTrue) {
      if (params.approveStatus === 'ZJ_APPROVED') {
        message.error('单据状态为已审批，信息不可修改');
      } else if (params.approveStatus === 'ZJ_PENGDING') {
        message.error('单据状态为审批中，信息不可修改');
      }
    } else {
      const { uploadOIDs } = this.state;
      console.log('22');
      console.log(uploadOIDs);
      OIDs.forEach(item => {
        uploadOIDs.push(item);
      });
      console.log('22');
      console.log(uploadOIDs);
      this.setState({
        uploadOIDs,
      });
    }
  };

  /**
   * 保存
   */
  saveApplicationInformation = async () => {
    const { form, applicationInformation } = this.props;
    const { user, isNew } = this.props;
    const { backMessage, uploadOIDs } = this.state;
    console.log('11');
    console.log(this.state);
    console.log(uploadOIDs);
    console.log(uploadOIDs[0]);
    form.validateFields((err, values) => {
      if (!err) {
        const saveData = {
          ...applicationInformation,
          accountDepositType: this.isArray(values.accountDepositType)
            ? values.accountDepositType[0].key
            : values.accountDepositType.key,
          accountDepositTypeName: this.isArray(values.accountDepositType)
            ? values.accountDepositType[0].label
            : values.accountDepositType.label,
          accountName: values.accountName ? values.accountName : '',
          accountNumber: applicationInformation.accountNumber,
          accountId: null,
          companyId: user.companyId,
          employeeId: user.id,
          employeeName: user.userName,
          accountProperty: this.isArray(values.accountProperty)
            ? values.accountProperty[0].key
            : values.accountProperty.key,
          accountPropertyName: this.isArray(values.accountProperty)
            ? values.accountProperty[0].label
            : values.accountProperty.label,
          status: this.isArray(values.status) ? values.status[0].key : values.status.key,
          statusDesc: this.isArray(values.status) ? values.status[0].label : values.status.key,
          accountSubjectsCode: values.accountSubjectsCode ? values.accountSubjectsCode : '',
          accountUse: this.isArray(values.accountUse)
            ? values.accountUse[0].key
            : values.accountUse.key,
          accountUseName: this.isArray(values.accountUse)
            ? values.accountUse[0].label
            : values.accountUse.key,
          bankAddress: values.bankAddress ? values.bankAddress : '',
          customerManager: values.customerManager ? values.customerManager : '',
          directFlag: this.isArray(values.directFlag)
            ? Number(values.directFlag[0].key)
            : Number(values.directFlag.key),
          finOfficialPrime: values.finOfficialPrime ? values.finOfficialPrime : '',
          interestRuleType: values.interestRuleType ? values.interestRuleType : '',
          openDate: moment(values.openDate).format() ? moment(values.openDate).format() : moment(),
          phone: values.phone ? values.phone : '',
          ukeyFlag: values.ukeyFlag ? values.ukeyFlag : false,
          ukey1ExpiryDate: this.isNull(values.ukey1ExpiryDate)
            ? moment(values.ukey1ExpiryDate).format()
            : '',
          ukey1Num: values.ukey1Num ? values.ukey1Num : '',
          ukey1RegisterName: this.isArray(values.ukey1RegisterName)
            ? values.ukey1RegisterName[0].name
            : values.ukey1RegisterName.name,
          ukey1UsingName: this.isArray(values.ukey1UsingName)
            ? values.ukey1UsingName[0].name
            : values.ukey1UsingName.name,
          ukey2ExpiryDate: this.isNull(values.ukey2ExpiryDate)
            ? moment(values.ukey2ExpiryDate).format()
            : '',
          depositTerm: this.isArray(values.depositTerm)
            ? values.depositTerm[0].key
            : values.depositTerm.key,
          ukey2Num: values.ukey2Num ? values.ukey2Num : '',
          ukey2RegisterName: this.isArray(values.ukey2RegisterName)
            ? values.ukey2RegisterName[0].name
            : values.ukey2RegisterName.name,
          ukey2UsingName: this.isArray(values.ukey2UsingName)
            ? values.ukey2UsingName[0].name
            : values.ukey2UsingName.name,
          ukey3ExpiryDate: this.isNull(values.ukey3ExpiryDate)
            ? moment(values.ukey3ExpiryDate).format()
            : '',
          ukey3Num: values.ukey3Num ? values.ukey3Num : '',
          ukey3RegisterName: this.isArray(values.ukey3RegisterName)
            ? values.ukey3RegisterName[0].name
            : values.ukey3RegisterName.name,
          ukey3UsingName: this.isArray(values.ukey3UsingName)
            ? values.ukey3UsingName[0].name
            : values.ukey3RegisterName.name,
          acountUkeyInfo: null,
          id: null,
          attachmentOid: uploadOIDs.join(','),
          versionNumber: values.versionNumber ? values.versionNumber : '',
        };

        if (
          (backMessage.data === null || typeof backMessage.data === 'undefined') &&
          isNew === true
        ) {
          saveData.accountId = applicationInformation.id;
          accountService
            .insertAccountModifyDetail(saveData)
            .then(res => {
              if (res.status === 200) {
                message.success('保存成功！');
                this.setState({
                  backMessage: res,
                  submitFlag: true,
                });
                this.onFlash(res.data.id);
              }
            })
            .catch(error => {
              message.error(error.errorCode);
            });
        } else {
          if (isNew === false) {
            saveData.accountId = applicationInformation.accountId;
            saveData.id = applicationInformation.id;
            saveData.versionNumber = applicationInformation.versionNumber;
          } else {
            saveData.accountId = backMessage.data.accountId;
            saveData.id = backMessage.data.id;
            saveData.versionNumber = applicationInformation.versionNumber;
          }
          accountService
            .updateAccountModifyDetail(saveData)
            .then(res => {
              if (res.status === 200) {
                message.success('修改成功！');
                this.setState({
                  submitFlag: true,
                });
                applicationInformation.versionNumber = res.data.versionNumber;
              }
            })
            .catch(error => {
              message.error(error.errorCode);
            });
        }
      }
    });
  };

  onFlash = id => {
    const { onFlash1 } = this.props;
    onFlash1(id);
  };

  onchangeIsCan = () => {
    const { onchangeIsCan } = this.props;
    onchangeIsCan();
  };

  isCan = value => {
    const { isCan } = this.state;
    const { form } = this.props;
    this.onchangeIsCan();
    console.log(isCan);
    if (value.key !== 'ZJ_TIME') {
      form.setFields({
        reminderDate: {
          value: '',
        },
        depositTerm: {
          value: [
            {
              key: null,
              label: '',
            },
          ],
        },
      });
      this.setState({
        isCan: true,
      });
    } else {
      this.setState({
        isCan: false,
      });
    }
  };

  /**
   * ukeyFlag变换
   */
  ukeyFlagChange = () => {
    const { applicationInformation, form } = this.props;
    applicationInformation.ukeyFlag = !applicationInformation.ukeyFlag;
    // location.reload(true)
    form.resetFields(applicationInformation.ukey1Num, applicationInformation.ukey2Num);
  };

  /**
   * 返回
   */
  onClickBack = () => {
    const { dispatch } = this.props;

    dispatch(
      routerRedux.push({
        pathname: `/account-manage/account-modify/account-modify`,
      })
    );
  };

  /**
   * 提交
   */
  submitDetail = async () => {
    const { applicationInformation, dispatch } = this.props;
    const { submitFlag } = this.state;
    const accountIdList = [];
    accountIdList.push(applicationInformation.id);
    if (submitFlag) {
      await accountService
        .submitAccountEdit(accountIdList)
        .then(res => {
          if (res.status === 200) {
            message.success('提交成功！');
            dispatch(
              routerRedux.push({
                pathname: `/account-manage/account-modify/account-modify`,
              })
            );
            this.setState({
              submitFlag: false,
            });
          }
        })
        .catch(err => {
          message.error(err.response.data.message);
          this.setState({
            submitFlag: false,
          });
        });
    } else {
      message.warning('请先保存，再提交！');
    }
  };

  render() {
    const {
      form: { getFieldDecorator },
      applicationInformation,
      isNew,
      isCan1,
      user,
    } = this.props;
    const { isCan } = this.state;

    let acountUkeyInfoCopy = {};
    if (typeof applicationInformation.acountUkeyInfo === 'undefined') {
      acountUkeyInfoCopy = applicationInformation;
    } else {
      acountUkeyInfoCopy = applicationInformation.acountUkeyInfo;
    }
    let ukeyflag = !applicationInformation.ukeyFlag;
    let isReadOnly = false;
    if (
      (applicationInformation.approveStatus === 'ZJ_APPROVED' ||
        applicationInformation.approveStatus === 'ZJ_PENGDING') &&
      isNew === false
    ) {
      isReadOnly = true;
      ukeyflag = true;
    }

    const formItemLayout = {
      labelCol: {
        span: 8,
      },
      wrapperCol: {
        span: 16,
      },
    };
    const { company } = this.props;
    const formItemLayout1 = {
      labelCol: {
        span: 4,
      },
      wrapperCol: {
        span: 16,
      },
    };
    const {
      accountPropertyList,
      fileList,
      accountUseList,
      accountDepositTypeList,
      directFlagList,
      accountStatusList,
      depositDurationList,
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
          <Form>
            <div
              style={{
                borderBottom: '1px solid rgb(236, 236, 236)',
              }}
            >
              <h3>基础信息:</h3>
            </div>
            <section style={{ paddingTop: '15px', paddingBottom: '15px', paddingLeft: '20px' }}>
              <Row>
                <Col span={6} style={{ display: isNew ? 'none' : '' }}>
                  银行账号：{applicationInformation.accountNumber}{' '}
                </Col>
                <Col span={6} style={{ display: isNew ? 'none' : '' }}>
                  单据编号：{applicationInformation.documentNumber}{' '}
                </Col>
                <Col span={6} style={{ display: isNew ? 'none' : '' }}>
                  {' '}
                  单据类型：{'账户变更'}{' '}
                </Col>
                <Col span={6} style={{ display: isNew ? 'none' : '' }}>
                  申请人：{user.userName}
                </Col>
              </Row>
              <Row>
                <Col span={6}>开户银行：{applicationInformation.openBankName} </Col>
                <Col span={6}>明细段值：{applicationInformation.branchBank} </Col>
                <Col span={6}>{applicationInformation.branchBankName} </Col>
                <Col span={6}>
                  开户日期：{moment(applicationInformation.openDate).format('YYYY-MM-DD')}{' '}
                </Col>
              </Row>
            </section>
            <div
              style={{
                borderBottom: '1px solid rgb(236, 236, 236)',
              }}
            >
              <h3>可变更信息:</h3>
            </div>
            <section style={{ paddingTop: '15px' }}>
              <Row>
                <Col span={6}>
                  <FormItem label="户名" {...formItemLayout}>
                    {getFieldDecorator('accountName', {
                      rules: [{ required: true }],
                      initialValue: applicationInformation.accountName
                        ? applicationInformation.accountName
                        : '',
                    })(
                      <Input
                        placeholder="请维护"
                        setfieldsvalue={applicationInformation.accountName}
                        disabled={isReadOnly}
                      />
                    )}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="账户性质" {...formItemLayout}>
                    {getFieldDecorator('accountProperty', {
                      rules: [{ required: true }],
                      initialValue: applicationInformation.accountProperty
                        ? [
                            {
                              key: applicationInformation.accountProperty,
                              label: applicationInformation.accountPropertyName,
                            },
                          ]
                        : '',
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
                      initialValue: applicationInformation.accountUse
                        ? [
                            {
                              key: applicationInformation.accountUse,
                              label: applicationInformation.accountUseName,
                            },
                          ]
                        : '',
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
                      initialValue: applicationInformation.accountDepositType
                        ? [
                            {
                              key: applicationInformation.accountDepositType,
                              label: applicationInformation.accountDepositTypeName,
                            },
                          ]
                        : '',
                    })(
                      <Select
                        labelInValue
                        placeholder="请选择"
                        onChange={this.isCan}
                        disabled={isReadOnly}
                        allowClear
                      >
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
                      initialValue: applicationInformation.finOfficialPrime
                        ? applicationInformation.finOfficialPrime
                        : '',
                    })(
                      <Input
                        disabled={isReadOnly}
                        placeholder="请维护"
                        setfieldsvalue={applicationInformation.finOfficialPrime}
                      />
                    )}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="开户公司" {...formItemLayout}>
                    {getFieldDecorator('companyName', {
                      rules: [{ required: true }],
                      initialValue: applicationInformation.companyName
                        ? [
                            {
                              id: applicationInformation.companyId,
                              name: applicationInformation.companyName,
                            },
                          ]
                        : '',
                    })(
                      <Chooser
                        type="company"
                        labelKey="name"
                        valueKey="id"
                        disabled={isReadOnly}
                        single
                        listExtraParams={{ setOfBooksId: company.setOfBooksId }}
                      />
                    )}
                  </FormItem>
                </Col>

                <Col span={6}>
                  <FormItem label="计息规则" {...formItemLayout}>
                    {getFieldDecorator('interestRuleType', {
                      rules: [{ required: true }],
                      initialValue: applicationInformation.interestRuleType
                        ? applicationInformation.interestRuleType
                        : '',
                    })(<Input placeholder="请维护" disabled={isReadOnly} />)}
                  </FormItem>
                </Col>
              </Row>
              <Row>
                <Col span={12}>
                  <FormItem label="分支行信息" {...formItemLayout1}>
                    {getFieldDecorator('branchBankName', {
                      rules: [{ required: true }],
                      initialValue: applicationInformation.branchBankName
                        ? applicationInformation.branchBankName
                        : '',
                    })(<Input placeholder="请维护" disabled={isReadOnly} />)}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="银行地址" {...formItemLayout}>
                    {getFieldDecorator('bankAddress', {
                      rules: [{ required: true }],
                      initialValue: applicationInformation.bankAddress
                        ? applicationInformation.bankAddress
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
                      initialValue: applicationInformation.customerManager
                        ? applicationInformation.customerManager
                        : '',
                    })(<Input placeholder="请维护" disabled={isReadOnly} />)}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="联系电话" {...formItemLayout}>
                    {getFieldDecorator('phone', {
                      rules: [{ required: true }],
                      initialValue: applicationInformation.phone
                        ? applicationInformation.phone
                        : '',
                    })(<Input placeholder="请维护" disabled={isReadOnly} />)}
                  </FormItem>
                </Col>
                <Col span={12}>
                  <FormItem label="核算科目" labelCol={{ span: 4 }} wrapperCol={{ span: 16 }}>
                    {getFieldDecorator('accountSubjectsCode', {
                      rules: [{ required: true }],
                      initialValue: applicationInformation.accountSubjectsCode
                        ? applicationInformation.accountSubjectsCode
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
                      initialValue: applicationInformation.directFlag
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
                      initialValue: applicationInformation.status
                        ? {
                            key: applicationInformation.status,
                            label: applicationInformation.statusDesc,
                          }
                        : '',
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
                        checked={applicationInformation.ukeyFlag}
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
                      rules: [{ required: !ukeyflag }],
                      initialValue: acountUkeyInfoCopy.ukey1Num ? acountUkeyInfoCopy.ukey1Num : '',
                    })(<Input placeholder="请维护" disabled={ukeyflag} />)}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="登记员工" {...formItemLayout}>
                    {getFieldDecorator('ukey1RegisterName', {
                      rules: [{ required: !ukeyflag }],
                      initialValue: acountUkeyInfoCopy.ukey1RegisterName
                        ? [
                            {
                              id: acountUkeyInfoCopy.ukey1RegisterName,
                              name: acountUkeyInfoCopy.ukey1RegisterName,
                            },
                          ]
                        : '',
                    })(
                      <Chooser
                        type="select_employee"
                        valueKey="id"
                        labelKey="name"
                        single
                        disabled={ukeyflag}
                      />
                    )}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="使用员工" {...formItemLayout}>
                    {getFieldDecorator('ukey1UsingName', {
                      rules: [{ required: !ukeyflag }],
                      initialValue: acountUkeyInfoCopy.ukey1UsingName
                        ? [
                            {
                              id: acountUkeyInfoCopy.ukey1UsingName,
                              name: acountUkeyInfoCopy.ukey1UsingName,
                            },
                          ]
                        : '',
                    })(
                      <Chooser
                        type="select_employee"
                        valueKey="id"
                        labelKey="name"
                        single
                        disabled={ukeyflag}
                      />
                    )}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="证书到期" {...formItemLayout}>
                    {getFieldDecorator('ukey1ExpiryDate', {
                      rules: [{ required: !ukeyflag }],
                      initialValue: acountUkeyInfoCopy.ukey1ExpiryDate
                        ? moment(acountUkeyInfoCopy.ukey1ExpiryDate)
                        : '',
                    })(<DatePicker format="YYYY-MM-DD" disabled={ukeyflag} />)}
                  </FormItem>
                </Col>
              </Row>
              <Row>
                <Col span={6}>
                  <FormItem label="UKEY编号2" {...formItemLayout}>
                    {getFieldDecorator('ukey2Num', {
                      rules: [{ required: !ukeyflag }],
                      initialValue: acountUkeyInfoCopy.ukey2Num ? acountUkeyInfoCopy.ukey2Num : '',
                    })(<Input placeholder="请维护" disabled={ukeyflag} />)}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="登记员工" {...formItemLayout}>
                    {getFieldDecorator('ukey2RegisterName', {
                      rules: [{ required: !ukeyflag }],
                      initialValue: acountUkeyInfoCopy.ukey2RegisterName
                        ? [
                            {
                              id: acountUkeyInfoCopy.ukey2RegisterName,
                              name: acountUkeyInfoCopy.ukey2RegisterName,
                            },
                          ]
                        : '',
                    })(
                      <Chooser
                        type="select_employee"
                        valueKey="id"
                        labelKey="name"
                        single
                        disabled={ukeyflag}
                      />
                    )}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="使用员工" {...formItemLayout}>
                    {getFieldDecorator('ukey2UsingName', {
                      rules: [{ required: !ukeyflag }],
                      initialValue: acountUkeyInfoCopy.ukey2UsingName
                        ? [
                            {
                              id: acountUkeyInfoCopy.ukey2UsingName,
                              name: acountUkeyInfoCopy.ukey2UsingName,
                            },
                          ]
                        : '',
                    })(
                      <Chooser
                        type="select_employee"
                        valueKey="id"
                        labelKey="name"
                        single
                        disabled={ukeyflag}
                      />
                    )}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="证书到期" {...formItemLayout}>
                    {getFieldDecorator('ukey2ExpiryDate', {
                      rules: [{ required: !ukeyflag }],
                      initialValue: acountUkeyInfoCopy.ukey2ExpiryDate
                        ? moment(acountUkeyInfoCopy.ukey2ExpiryDate)
                        : '',
                    })(<DatePicker format="YYYY-MM-DD" disabled={ukeyflag} />)}
                  </FormItem>
                </Col>
              </Row>
              <Row>
                <Col span={6}>
                  <FormItem label="UKEY编号3" {...formItemLayout}>
                    {getFieldDecorator('ukey3Num', {
                      rules: [{ required: !ukeyflag }],
                      initialValue: acountUkeyInfoCopy.ukey3Num ? acountUkeyInfoCopy.ukey3Num : '',
                    })(<Input placeholder="请维护" disabled={ukeyflag} />)}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="登记员工" {...formItemLayout}>
                    {getFieldDecorator('ukey3RegisterName', {
                      rules: [{ required: !ukeyflag }],
                      initialValue: acountUkeyInfoCopy.ukey3RegisterName
                        ? [
                            {
                              id: acountUkeyInfoCopy.ukey3RegisterName,
                              name: acountUkeyInfoCopy.ukey3RegisterName,
                            },
                          ]
                        : '',
                    })(
                      <Chooser
                        type="select_employee"
                        valueKey="id"
                        labelKey="name"
                        single
                        disabled={ukeyflag}
                      />
                    )}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="使用员工" {...formItemLayout}>
                    {getFieldDecorator('ukey3UsingName', {
                      rules: [{ required: !ukeyflag }],
                      initialValue: acountUkeyInfoCopy.ukey3UsingName
                        ? [
                            {
                              id: acountUkeyInfoCopy.ukey3UsingName,
                              name: acountUkeyInfoCopy.ukey3UsingName,
                            },
                          ]
                        : '',
                    })(
                      <Chooser
                        type="select_employee"
                        valueKey="id"
                        labelKey="name"
                        single
                        disabled={ukeyflag}
                      />
                    )}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="证书到期" {...formItemLayout}>
                    {getFieldDecorator('ukey3ExpiryDate', {
                      rules: [{ required: !ukeyflag }],
                      initialValue: acountUkeyInfoCopy.ukey3ExpiryDate
                        ? moment(acountUkeyInfoCopy.ukey3ExpiryDate)
                        : '',
                    })(<DatePicker format="YYYY-MM-DD" disabled={ukeyflag} />)}
                  </FormItem>
                </Col>
              </Row>
              <Row>
                <Col span={6}>
                  <FormItem label="存款期限" {...formItemLayout}>
                    {getFieldDecorator('depositTerm', {
                      rules: [{ required: !(isCan1 || isCan) }],
                      initialValue: applicationInformation.depositTerm
                        ? [
                            {
                              key: applicationInformation.depositTerm,
                              label: applicationInformation.depositTermName,
                            },
                          ]
                        : [],
                    })(
                      <Select
                        labelInValue
                        placeholder="请选择"
                        disabled={isCan1 || isCan || isReadOnly}
                        allowClear
                      >
                        {depositDurationList.map(option => {
                          return <Option key={option.value}>{option.name}</Option>;
                        })}
                      </Select>
                    )}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="提醒时间" {...formItemLayout}>
                    {getFieldDecorator('reminderDate', {
                      rules: [{ required: !(isCan1 || isCan) }],
                      initialValue: applicationInformation.reminderDate
                        ? moment(applicationInformation.reminderDate)
                        : '',
                    })(<DatePicker format="YYYY-MM-DD" disabled={isCan1 || isCan || isReadOnly} />)}
                  </FormItem>
                </Col>
              </Row>
            </section>
            <section style={{ paddingTop: '15px' }}>
              <Row>
                <Col span={9}>
                  <FormItem label="说明" labelCol={{ span: 4 }} wrapperCol={{ span: 16 }}>
                    {getFieldDecorator('accountSubjectsCode', {
                      rules: [{ required: true }],
                      initialValue: applicationInformation.accountSubjectsCode
                        ? applicationInformation.accountSubjectsCode
                        : '',
                    })(<Input placeholder="请维护" disabled={isReadOnly} />)}
                  </FormItem>
                </Col>
              </Row>
            </section>
            <section>
              <Row>
                <Col span={6}>
                  <FormItem label="附件信息" {...formItemLayout}>
                    {getFieldDecorator('attachmentOID', {
                      initialValue: applicationInformation.attachmentOid
                        ? applicationInformation.attachmentOid
                        : '',
                    })(
                      <Upload
                        attachmentType="CONTRACT"
                        uploadUrl={`${config.baseUrl}/api/upload/static/attachment`}
                        fileNum={9}
                        uploadHandle={this.handleUpload}
                        defaultFileList={fileList}
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
function mapStateToProps(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
  };
}

export default connect(mapStateToProps)(Form.create()(modifyAccountAddInformation));

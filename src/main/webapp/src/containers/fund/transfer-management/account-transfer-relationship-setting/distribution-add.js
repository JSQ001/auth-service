import React from 'react';
import { connect } from 'dva';
import moment from 'moment';
import {
  Form,
  Input,
  Button,
  Select,
  Row,
  Col,
  InputNumber,
  DatePicker,
  message,
  TimePicker,
} from 'antd';
import Lov from 'widget/Template/lov';
import distributionService from './distribution.service';

const FormItem = Form.Item;
const { Option } = Select;

class DistributionAdd extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      imputationType: [], // 归集方式
      imputationRate: [], // 归集频率
      autoMakeList: [{ value: 1, name: '开启' }, { value: 0, name: '关闭' }], // 自动补齐
      editModel: {},
      isGatherRate: false, //
      isautoshow: false,
      ationRateValue: '',
      ationRateValue1: false,
    };
  }

  componentDidMount() {
    const { params, addId } = this.props;

    // console.log(this.props);
    // console.log(params, addId);
    if (addId) {
      this.setState({
        headOId: addId,
      });
      if (params) {
        this.setState({
          editModel: params,
        });
      }
    }
    this.getImputationType();
    this.getImputationRate();
  }

  /**
   * 归集方式
   */
  getImputationType() {
    this.getSystemValueList('ZJ_COLLECTING_METHOD')
      .then(res => {
        if (res.data.values.length > 0) {
          this.setState({
            imputationType: res.data.values,
          });
        }
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  }

  /**
   * 归集频率
   */
  getImputationRate() {
    this.getSystemValueList('ZJ_COLLECTING_TIME')
      .then(res => {
        if (res.data.values.length > 0) {
          this.setState({
            imputationRate: res.data.values,
          });
        }
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  }

  onChange = values => {
    // console.log(values)
    this.setState({
      accountOId: values.id || '',
      accountName: values.accountName || '',
      // accountNumber: values.accountNumber || '',
      companyName: values.companyName || '',
      openBankName: values.openBankName || '',
      companyOId: values.companyId || '',
      bankOCode: values.openBank || '',
    });
  };

  /**
   * 保存或者修改
   */
  handleSave = e => {
    e.preventDefault();
    const { companyOId, accountOId, bankOCode, headOId } = this.state;
    const { params } = this.props;
    const {
      form: { getFieldsValue },
    } = this.props;
    const data = getFieldsValue();
    console.log('data');
    console.log(data);
    let saveDate = {};
    saveDate = {
      id: params ? params.id : '',
      accountId: accountOId || params.accountId,
      headId: headOId || '',
      bankAccount: data.bankAccount.accountNumber,
      bankAccountName: data.bankAccountName,
      bankCode: bankOCode || params.bankCode,
      companyId: companyOId || params.companyId,
      gatherFrequency: data.gatherFrequency.key || params.gatherFrequency,
      gatherType: data.gatherType.key || params.gatherType,
      autoCompletion: data.autoCompletion.key,
      gatherRate: data.gatherRate,
      amount: data.amount,
      gatherDate: data.gatherDate,
      gatherWeek: data.gatherWeek || '',
      gatherTime: moment(data.gatherTime).format('HH:mm'),
      startDate: data.startDate ? moment(data.startDate) : '',
      // startDateDesc: moment(data.startDateDesc)
      //   .format()
      //   .slice(0, 10),
    };
    // console.log(saveDate)
    distributionService.addDistributionList(saveDate).then(res => {
      if (res.status === 200) {
        if (saveDate.id) {
          message.success('修改成功');
          this.handleClose('save');
        } else {
          message.success('保存成功');
          this.handleClose('save');
        }
      }
    });
    // .catch(error => {
    //   console.log(error)
    //   // message.error(error.response.data.message)
    // })
  };

  selectAtionRate = value => {
    const { onNew2 } = this.props;
    onNew2();
    console.log(value);
    if (value.key === 'EVERYDAY') {
      this.setState({
        ationRateValue: 'EVERYDAY',
      });
    }
    if (value.key === 'EVERYMONTH') {
      this.setState({
        ationRateValue: 'EVERYMONTH',
      });
    }
    if (value.key === 'EVERYWEEK') {
      this.setState({
        ationRateValue: 'EVERYWEEK',
      });
    }
    if (value.key === 'TRADING_DAY') {
      this.setState({
        ationRateValue: 'TRADING_DAY',
      });
    }
    if (value.key === 'WORKING_DAY') {
      this.setState({
        ationRateValue: 'WORKING_DAY',
      });
    }
    if (value.key) {
      this.setState({
        ationRateValue1: true,
      });
    }
  };

  /**
   * 取消
   */
  handleClose = value => {
    const { onClose } = this.props;
    onClose(value);
  };

  selectGatherType = value => {
    const { onNew } = this.props;
    onNew();
    console.log(value);
    if (value.label === '固定余额') {
      this.setState({
        isGatherRate: false,
        isautoshow: false,
      });
    }
    if (value.label === '固定比例') {
      console.log('sssyy');
      this.setState({
        isGatherRate: true,
        isautoshow: true,
      });
    }
    if (value.label === '固定金额') {
      this.setState({
        isGatherRate: false,
        isautoshow: true,
      });
    }
  };

  render() {
    const {
      form: { getFieldDecorator },
      isautoshow1,
      isGatherRate1,
      isNew,
      isNew2,
      ationRateValue2,
      ationRateValue3,
      // user,
      // company,
    } = this.props;
    const formItemLayout1 = {
      labelCol: {
        span: 7,
      },
      wrapperCol: {
        span: 12,
      },
    };
    const formItemLayout2 = {
      labelCol: {
        span: 7,
      },
      wrapperCol: {
        span: 10,
      },
    };
    const formItemLayout = {
      labelCol: {
        span: 10,
      },
      wrapperCol: {
        span: 12,
      },
    };
    const formItemLayout3 = {
      labelCol: {
        span: 9,
      },
    };
    const {
      imputationType,
      imputationRate,
      autoMakeList,
      accountName,
      companyName,
      openBankName,
      editModel,
      isGatherRate,
      isautoshow,
      ationRateValue,
      ationRateValue1,
      // accountNumber,
    } = this.state;
    console.log('ationRateValue');
    console.log(ationRateValue);
    // this.selectGatherType = value => {
    //     console.log(value)
    //     if (value.label === '固定余额') {
    //       this.isGatherRate= false;
    //     }
    //     if (value.label === '固定比例') {
    //       console.log('sssyy11111')
    //       this.isGatherRate= true
    //     }
    //     if (value.label === '固定金额') {
    //         this.isGatherRate= false;
    //     }
    //   };
    return (
      <div>
        <Form>
          <FormItem label="子账号" {...formItemLayout1}>
            {getFieldDecorator('bankAccount', {
              // rules: [{ required: true }],
              // initialValue: false,
              // initialValue: accountNumber || editModel.bankAccount || '',
              initialValue: { id: editModel.id, accountNumber: editModel.bankAccount },
            })(
              <Lov
                code="bankaccount_choose"
                valueKey="id"
                labelKey="accountNumber"
                single
                onChange={this.onChange}
              />
            )}
          </FormItem>
          <FormItem label="子账户" {...formItemLayout1}>
            {getFieldDecorator('bankAccountName', {
              // rules: [{ required: true }],
              initialValue: accountName || editModel.bankAccountName || '',
            })(<Input AUTOCOMPLETE="off" disabled />)}
          </FormItem>
          <FormItem label="所属机构" {...formItemLayout1}>
            {getFieldDecorator('companyId', {
              // rules: [{ required: true }],
              initialValue: companyName || editModel.companyName || '',
              // initialValue: typeof updateDateU.id === 'undefined' ? true : updateDateU.queryFlag,
            })(<Input AUTOCOMPLETE="off" disabled />)}
          </FormItem>
          <FormItem label="所属银行" {...formItemLayout1}>
            {getFieldDecorator('bankCode', {
              // rules: [{ required: true }],
              initialValue: openBankName || editModel.bankName || '',
              // initialValue: typeof updateDateU.id === 'undefined' ? true : updateDateU.queryFlag,
            })(<Input AUTOCOMPLETE="off" disabled />)}
          </FormItem>
          <FormItem label="归集方式:" {...formItemLayout1}>
            {getFieldDecorator('gatherType', {
              initialValue: editModel
                ? [{ key: editModel.gatherType, label: editModel.gatherTypeDesc }]
                : [],
            })(
              <Select labelInValue placeholder="请选择" onSelect={this.selectGatherType}>
                {imputationType.map(option => {
                  return <Option key={option.value}>{option.name}</Option>;
                })}
              </Select>
            )}
          </FormItem>
          <Row>
            <Col span={5} offset={4}>
              <FormItem label="金额" {...formItemLayout2}>
                {getFieldDecorator('amount', {
                  initialValue: editModel.amount || '',
                })(
                  <InputNumber disabled={isNew ? isGatherRate : isGatherRate1} AUTOCOMPLETE="off" />
                )}
              </FormItem>
            </Col>
            <Col span={5}>
              <FormItem label="比例" {...formItemLayout2}>
                {getFieldDecorator('gatherRate', {
                  initialValue: editModel.gatherRate || '',
                })(
                  <InputNumber
                    disabled={isNew ? !isGatherRate : !isGatherRate1}
                    AUTOCOMPLETE="off"
                    formatter={value => `${value}%`}
                    parser={value => value.replace('%', '')}
                  />
                )}
              </FormItem>
            </Col>
            <Col span={6}>
              <FormItem label="自动补齐" {...formItemLayout}>
                {getFieldDecorator('autoCompletion', {
                  initialValue:
                    editModel.autoCompletion === 1
                      ? [{ key: 1, label: '开启' }]
                      : [{ key: 0, label: '关闭' }],
                })(
                  <Select
                    labelInValue
                    placeholder="请选择"
                    disabled={isNew ? isautoshow : isautoshow1}
                  >
                    {autoMakeList.map(option => {
                      return <Option key={option.value}>{option.name}</Option>;
                    })}
                  </Select>
                )}
              </FormItem>
            </Col>
          </Row>
          <FormItem label="归集频率:" {...formItemLayout1}>
            {getFieldDecorator('gatherFrequency', {
              initialValue: editModel
                ? [{ key: editModel.gatherFrequency, label: editModel.gatherFrequency }]
                : [],
            })(
              <Select labelInValue placeholder="请选择" onSelect={this.selectAtionRate}>
                {imputationRate.map(option => {
                  return <Option key={option.value}>{option.name}</Option>;
                })}
              </Select>
            )}
          </FormItem>
          <Row>
            <Col span={5} offset={4}>
              <FormItem label="日期" {...formItemLayout2}>
                {getFieldDecorator('gatherDate', {
                  initialValue: editModel.gatherDate || '',
                })(
                  <InputNumber
                    min={1}
                    max={31}
                    formatter={value => `${value}`}
                    parser={value => value.replace('^[0-9]*[1-9][0-9]*$')}
                    precision="0"
                    AUTOCOMPLETE="off"
                    disabled={
                      isNew2
                        ? !(ationRateValue === 'EVERYMONTH')
                        : !(ationRateValue2 === 'EVERYMONTH')
                    }
                  />
                )}
              </FormItem>
            </Col>
            <Col span={5}>
              <FormItem label="周" {...formItemLayout2}>
                {getFieldDecorator('gatherWeek', {
                  initialValue: editModel.gatherWeek || '',
                })(
                  <InputNumber
                    min={1}
                    max={7}
                    formatter={value => `${value}`}
                    parser={value => value.replace('^[0-9]*[1-9][0-9]*$')}
                    precision="0"
                    AUTOCOMPLETE="off"
                    disabled={
                      isNew2
                        ? !(ationRateValue === 'EVERYWEEK')
                        : !(ationRateValue2 === 'EVERYWEEK')
                    }
                  />
                )}
              </FormItem>
            </Col>
            <Col span={6}>
              <FormItem label="时间" {...formItemLayout}>
                {getFieldDecorator('gatherTime', {
                  initialValue: moment(editModel.gatherTime) || '',
                })(
                  // <Input
                  //   formatter={value => `${value}`}
                  //   parser={value => value.replace('([01]\d|2[0-3]):([0-5]\d)')}
                  //   precision="0"
                  //   AUTOCOMPLETE="off"
                  //   disabled={!ationRateValue1}
                  // />
                  <TimePicker
                    defaultValue={moment('12:08', 'HH:mm')}
                    format="HH:mm"
                    disabled={isNew2 ? !ationRateValue1 : !ationRateValue3}
                  />
                )}
              </FormItem>
            </Col>
          </Row>
          <FormItem label="开始时间" {...formItemLayout3}>
            {getFieldDecorator('startDate', {
              // rules: [{ required: true }],
              initialValue: moment(editModel.startDateDesc),
              // initialValue: typeof updateDateU.id === 'undefined' ? true : updateDateU.queryFlag,
            })(<DatePicker AUTOCOMPLETE="off" size="100px" format="YYYY-MM-DD" />)}
          </FormItem>
          <div className="slide-footer">
            <Button
              type="primary"
              htmlType="submit"
              style={{ margin: '0 20px' }}
              onClick={this.handleSave}
            >
              保存
            </Button>
            <Button onClick={this.handleClose}>取消</Button>
          </div>
        </Form>
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
export default connect(map)(Form.create()(DistributionAdd));

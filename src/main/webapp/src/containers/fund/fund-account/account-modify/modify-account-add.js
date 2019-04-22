import React from 'react';
// import Chooser from 'widget/chooser';
import { Form, Input, Card, message, Row, Col } from 'antd';
import { connect } from 'dva';
import Lov from 'widget/Template/lov';
import accountService from './modify-account.service';
import ModifyAccountAddInformation from './modify-account-add-information';

const FormItem = Form.Item;
class ModifyAccountAdd extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      selectDate: {}, // 银行账户信息选中的值
      isNew: true, // 是否为新建单据
      isCan1: false, // 是否为新建单据
      isDisable: true, // 可用标志
      applicationInformation: {},
      backInformation: [],
    };
  }

  componentDidMount() {
    const { match } = this.props;
    // console.log(match.params.id)
    if (match.params.id !== 'new') {
      // console.log(match.params.id!=='new')
      this.setState({
        isNew: false,
      });
      this.getModifyAccountDetail(match.params.id);
    }
  }

  /**
   * 点击确定
   */
  onOk = values => {
    const { isCan1 } = this.state;
    this.setState({
      applicationInformation: values,
    });
    if (values.accountDepositType === 'ZJ_TIME') {
      console.log(isCan1); //
      this.setState({
        isCan1: false,
      });
    } else {
      this.setState({
        isCan1: true,
      });
    }
  };

  onchangeIsCan = () => {
    this.setState({
      isCan1: false,
    });
  };

  /**
   * 根据头ID获取详情
   */
  getModifyAccountDetail = id => {
    const { isCan1 } = this.state;
    accountService
      .getModifyAccountDetail(id)
      .then(res => {
        this.setState({
          applicationInformation: res.data,
          isDisable: true,
        });
        console.log(isCan1);
        if (res.data.accountDepositType === 'ZJ_TIME') {
          this.setState({
            isCan1: false,
          });
        } else {
          this.setState({
            isCan1: true,
          });
        }
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  /**
   * 保存后，重新加载数据
   */
  onFlash = id => {
    accountService
      .getModifyAccountDetail(id)
      .then(res => {
        this.setState({
          applicationInformation: res.data,
          backInformation: res.data.documentNumber,
        });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  modalSearch = () => {
    const {
      form: { getFieldValue },
    } = this.props;
    this.getBackList(getFieldValue('modalAccount'));
  };

  modalSearchClear = () => {
    const {
      form: { resetFields },
    } = this.props;
    resetFields('modalAccount');
  };

  render() {
    const { applicationInformation, isNew, backInformation, isDisable, isCan1 } = this.state;
    const { selectDate } = this.state;
    const { user } = this.props;
    const {
      form: { getFieldDecorator },
    } = this.props;
    const formItemLayout = {
      labelCol: {
        span: 8,
      },
      wrapperCol: {
        span: 12,
      },
    };

    return (
      <div>
        {isNew ? (
          <Card
            style={{
              boxShadow: '0 2px 2px rgba(0, 0, 0, 0.15)',
              marginRight: 15,
              marginLeft: 15,
              marginTop: 1,
            }}
          >
            <Form layout="inline">
              <Row>
                <Col span={6}>
                  <FormItem label="银行账号" {...formItemLayout}>
                    {getFieldDecorator('accountNumber', {
                      rules: [{ required: true }],
                      initialValue: selectDate.accountNumber ? selectDate.accountNumber : '',
                    })(
                      <Lov
                        code="bank_account_all"
                        valueKey="id"
                        labelKey="accountNumber"
                        single
                        onChange={this.onOk}
                      />
                    )}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="单据编号" {...formItemLayout}>
                    {getFieldDecorator('documentNumber', {
                      rules: [{ required: true }],
                      initialValue: backInformation,
                    })(<Input setfieldsvalue={applicationInformation.documentNumber} disabled />)}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="单据类型" {...formItemLayout}>
                    {<Input defaultValue="账户变更" disabled />}
                  </FormItem>
                </Col>
                <Col span={6}>
                  <FormItem label="申请人" {...formItemLayout}>
                    {getFieldDecorator('userName', {
                      rules: [{ required: true }],
                      initialValue: user.userName ? user.userName : '',
                    })(<Input setfieldsvalue={user.userName} disabled />)}
                  </FormItem>
                </Col>
              </Row>
            </Form>
          </Card>
        ) : null}
        <ModifyAccountAddInformation
          applicationInformation={applicationInformation}
          onFlash1={this.onFlash}
          isNew={isNew}
          isDisable={isDisable}
          isCan1={isCan1}
          onchangeIsCan={this.onchangeIsCan}
        />
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

export default connect(map)(Form.create()(ModifyAccountAdd));

/* eslint-disable */
import React from 'react';
import {
  Form,
  Input,
  message,
  Select,
  Col,
  Row,
  Button,
  InputNumber,
  Spin,
  DatePicker,
  Icon,
} from 'antd';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import moment from 'moment';
const FormItem = Form.Item;
const { MonthPicker } = DatePicker;
import Chooser from 'widget/chooser';
import getAccountBalanceService from './get-account-balance.service';
import Lov from 'widget/Template/lov';
class getAccountBalance extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      params: {},
      data: [],
      periodName: '',
    };
  }
  //返回科目余额接口界面
  onBackClick = e => {
    //e.preventDefault();
    this.props.dispatch(
      routerRedux.replace({
        pathname: `/inter-management/acc-balance-interface/acc-balance-interface`,
      })
    );
  };

  handleSubmit = preps => {
    this.props.form.validateFields((err, values) => {
      const paramss = {};
      if (values.ledgerName != 'undefine' && values.ledgerName != null) {
        paramss.ledgerName = values.ledgerName.setOfBooksName;
      }
      if (values.segment1 != 'undefine' && values.segment1 != null) {
        paramss.segment1 = values.segment1.companyCode;
      }
      if (values.segment3 != 'undefine' && values.segment3 != null) {
        paramss.segment3 = values.segment3.responsibilityCenterCode;
      }
      if (
        this.state.periodName != 'undefine' &&
        this.state.periodName != null &&
        this.state.periodName != ''
      ) {
        paramss.periodName = this.state.periodName;
      }
      getAccountBalanceService
        .getAccountBalance(paramss)
        .then(response => {
          this.onBackClick();
        })
        .catch(err => {
          this.setState({ loading: false });
          this.props.onError && this.props.onError();
        });
    });
  };
  onChange = (date, dateString) => {
    this.setState({
      periodName: dateString,
    });
  };

  render() {
    const { params } = this.props;
    const { form } = this.props;
    const { data } = this.state;
    const { getFieldDecorator } = form;
    const Option = Select.Option;
    const formItemLayout = {
      labelCol: {
        xs: { span: 8 },
        sm: { span: 8 },
      },
      wrapperCol: {
        xs: { span: 8 },
        sm: { span: 8 },
      },
    };

    return (
      <div>
        <Row gutter={24}>
          <Form onSubmit={this.handleSubmit}>
            <Row style={{ textAlign: 'center' }}>
              <Form.Item {...formItemLayout} label="账套">
                {getFieldDecorator('ledgerName', {
                  rules: [{ required: true, message: this.$t('common.please.enter') }],
                  //initialValue: data.outputTaxDiskFirm
                })(
                  <Lov
                    code="setOfBooks"
                    valueKey="setOfBooksCode"
                    labelKey="setOfBooksName"
                    single
                    extraParams={{ tenantId: this.props.company.tenantId }}
                  />
                )}
              </Form.Item>
              <FormItem {...formItemLayout} label={'公司'}>
                {getFieldDecorator('segment1', {
                  rules: [
                    {
                      required: false,
                      message: this.$t('common.please.enter'),
                    },
                  ],
                  //  initialValue: params.companyCode && [{ id: params.companyCode, taxpayerName: params.taxpayerName }],
                })(
                  <Lov
                    code="company_lov"
                    valueKey="companyCode"
                    labelKey="companyName"
                    single
                    extraParams={{ tenantId: this.props.company.tenantId }}
                  />
                )}
              </FormItem>

              <FormItem {...formItemLayout} label={'成本中心'}>
                {getFieldDecorator('segment3', {
                  rules: [
                    {
                      required: false,
                      message: this.$t('common.please.enter'),
                    },
                  ],
                  // initialValue: params.taxpayerId && [{ id: params.taxpayerId, taxpayerName: params.taxpayerName }],
                })(
                  <Lov
                    code="responsibilityCenter"
                    valueKey="responsibilityCenterCode"
                    labelKey="responsibilityCenterName"
                    single
                    extraParams={{ setOfBooksId: this.props.company.setOfBooksId }}
                  />
                )}
              </FormItem>
              <FormItem {...formItemLayout} label={'期间'}>
                {getFieldDecorator('periodName', {
                  rules: [
                    {
                      required: false,
                      message: this.$t('common.please.enter'),
                    },
                  ],
                })(<MonthPicker onChange={this.onChange} placeholder="Select month" />)}
                <br />
              </FormItem>
            </Row>
            <Row style={{ textAlign: 'center' }}>
              <Button type="primary" htmlType="submit" loading={this.state.loading}>
                提交
              </Button>
              <Button onClick={this.onBackClick}>返回</Button>
            </Row>
          </Form>
        </Row>
      </div>
    );
  }
}
function mapStateToProps(state) {
  return {
    company: state.user.company,
    user: state.user.currentUser,
  };
}
const wrapperedGetAccountBalance = Form.create()(getAccountBalance);

export default connect(mapStateToProps)(wrapperedGetAccountBalance);

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
const { MonthPicker, RangePicker } = DatePicker;
const monthFormat = 'YYYY/MM';
import Chooser from 'widget/chooser';
import getAccountBalanceService from './get-account-balance.service';
class getAccountBalance extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      params: {},
      data: [],
    };
  }
  //返回科目余额接口界面
  onBackClick = e => {
    e.preventDefault();
    this.props.dispatch(
      routerRedux.replace({
        pathname: `/inter-management/acc-balance-interface/acc-balance-interface`,
      })
    );
  };

  handleSubmit = preps => {
    this.props.form.validateFields((err, values) => {
      const params = {};
      params.setOfbook = values.setOfbook;
      params.segment1 = values.segment1;
      params.segment2 = values.segment2;
      params.periodName = values.periodName;

      getAccountBalanceService
        .getAccountBalance(params)
        .then(response => {
          this.onBackClick();
        })
        .catch(err => {
          this.setState({ loading: false });
          this.props.onError && this.props.onError();
        });
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
                {getFieldDecorator('setOfbook', {
                  rules: [{ required: true, message: this.$t('common.please.enter') }],
                  initialValue: data.outputTaxDiskFirm,
                })(
                  <Select placeholder="请选择" style={{ width: '100%' }}>
                    <Option value="方正证券">方正证券</Option>
                    <Option value="方正中期期货">方正中期期货</Option>
                    <Option value="方正和生">方正和生</Option>
                    <Option value="方正香港">方正香港</Option>
                  </Select>
                )}
              </Form.Item>
              <FormItem {...formItemLayout} label={'机构'}>
                {getFieldDecorator('segment1', {
                  rules: [
                    {
                      required: false,
                      message: this.$t('common.please.enter'),
                    },
                  ],
                  //  initialValue: params.companyCode && [{ id: params.companyCode, taxpayerName: params.taxpayerName }],
                })(
                  <Chooser
                    labelKey="segment1Des"
                    valueKey="segment1"
                    type="company_detail"
                    single
                  />
                )}
              </FormItem>
              <FormItem {...formItemLayout} label={'成本中心'}>
                {getFieldDecorator('segment2', {
                  rules: [
                    {
                      required: false,
                      message: this.$t('common.please.enter'),
                    },
                  ],
                  // initialValue: params.taxpayerId && [{ id: params.taxpayerId, taxpayerName: params.taxpayerName }],
                })(
                  <Chooser labelKey="segment2Des" valueKey="segment2" type="specific_item" single />
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
                  //  initialValue: params.companyCode && [{ id: params.companyCode, taxpayerName: params.taxpayerName }],
                })(
                  <MonthPicker
                    defaultValue={moment('2019/04', monthFormat)}
                    valueKey="periodName"
                    format={monthFormat}
                  />
                )}
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

const wrapperedGetAccountBalance = Form.create()(getAccountBalance);

export default connect()(wrapperedGetAccountBalance);

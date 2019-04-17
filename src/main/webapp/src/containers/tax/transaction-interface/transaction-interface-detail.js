import React, { Component } from 'react';
import { connect } from 'dva';
import moment from 'moment';
import { Form, Row, Col, Input, Button, DatePicker } from 'antd';
const FormItem = Form.Item;
class TaxVatTranInterface extends Component {
  constructor(props) {
    super(props);
    this.state = {
      visibel: false,
      loading: false,
      period: {},
    };
  }
  componentWillMount() {
    console.log(this.props.params.period);
    this.setState({
      period: this.props.params.period,
    });
  }
  render() {
    const { getFieldDecorator } = this.props.form;
    const { period, loading } = this.state;
    const dateFormat = 'YYYY-MM-DD';
    return (
      <Form>
        <Row>
          <Col span={6} offset={1}>
            <FormItem label="交易流水号">
              {getFieldDecorator('tranNum', {
                initialValue: period ? period.tranNum : '',
              })(<Input disabled={true} />)}
            </FormItem>
          </Col>
          <Col span={6} offset={1}>
            <FormItem label="客户编号">
              {getFieldDecorator('clientAcc', {
                initialValue: period ? period.clientAcc : '',
              })(<Input disabled={true} />)}
            </FormItem>
          </Col>
          <Col span={6} offset={1}>
            <FormItem label="客户纳税人名称">
              {getFieldDecorator('taxpayerName', {
                initialValue: period ? period.taxpayerName : '',
              })(<Input disabled={true} />)}
            </FormItem>
          </Col>
        </Row>

        <Row>
          <Col span={6} offset={1}>
            <FormItem label="机构">
              {getFieldDecorator('org', {
                initialValue: period ? period.org : '',
              })(<Input disabled={true} />)}
            </FormItem>
          </Col>
          <Col span={6} offset={1}>
            <FormItem label="责任中心（部门）">
              {getFieldDecorator('costCenter', {
                initialValue: period ? period.costCenter : '',
              })(<Input disabled={true} />)}
            </FormItem>
          </Col>
          <Col span={6} offset={1}>
            <FormItem label="交易日期">
              {getFieldDecorator('tranDate', {
                initialValue: period ? period.tranDate : '',
              })(<Input disabled={true} />)}
            </FormItem>
          </Col>
        </Row>

        <Row>
          <Col span={6} offset={1}>
            <FormItem label="交易币种">
              {getFieldDecorator('currencyCode', {
                initialValue: period ? period.currencyCode : '',
              })(<Input disabled={true} />)}
            </FormItem>
          </Col>
          <Col span={6} offset={1}>
            <FormItem label="原币种金额">
              {getFieldDecorator('tranAmount', {
                initialValue: period ? period.tranAmount : '',
              })(<Input disabled={true} />)}
            </FormItem>
          </Col>
          <Col span={6} offset={1}>
            <FormItem label="本位币金额">
              {getFieldDecorator('functionalAmount', {
                initialValue: period ? period.functionalAmount : '',
              })(<Input disabled={true} />)}
            </FormItem>
          </Col>
        </Row>

        <Row>
          <Col span={6} offset={1}>
            <FormItem label="原币销售额">
              {getFieldDecorator('sales', {
                initialValue: period ? period.sales : '',
              })(<Input disabled={true} />)}
            </FormItem>
          </Col>
          <Col span={6} offset={1}>
            <FormItem label="本币销售额">
              {getFieldDecorator('funSales', {
                initialValue: period ? period.funSales : '',
              })(<Input disabled={true} />)}
            </FormItem>
          </Col>
          <Col span={6} offset={1}>
            <FormItem label="原币折扣额">
              {getFieldDecorator('dsctAmount', {
                initialValue: period ? period.dsctAmount : '',
              })(<Input disabled={true} />)}
            </FormItem>
          </Col>
        </Row>
        <Row>
          <Col span={6} offset={1}>
            <FormItem label="本币折扣额">
              {getFieldDecorator('funDsctAmount', {
                initialValue: period ? period.funDsctAmount : '',
              })(<Input disabled={true} />)}
            </FormItem>
          </Col>
          <Col span={6} offset={1}>
            <FormItem label="原币扣除额">
              {getFieldDecorator('deductionAmount', {
                initialValue: period ? period.deductionAmount : '',
              })(<Input disabled={true} />)}
            </FormItem>
          </Col>
          <Col span={6} offset={1}>
            <FormItem label="本币扣除额">
              {getFieldDecorator('funDeductionAmount', {
                initialValue: period ? period.funDeductionAmount : '',
              })(<Input disabled={true} />)}
            </FormItem>
          </Col>
        </Row>

        <Row>
          <Col span={6} offset={1}>
            <FormItem label="原币销项税额">
              {getFieldDecorator('outTaxes', {
                initialValue: period ? period.outTaxes : '',
              })(<Input disabled={true} />)}
            </FormItem>
          </Col>
          <Col span={6} offset={1}>
            <FormItem label="本币销项税额">
              {getFieldDecorator('funOutTaxes', {
                initialValue: period ? period.funOutTaxes : '',
              })(<Input disabled={true} />)}
            </FormItem>
          </Col>
          <Col span={6} offset={1}>
            <FormItem label="本币交易说明">
              {getFieldDecorator('tranDesc', {
                initialValue: period ? period.tranDesc : '',
              })(<Input disabled={true} />)}
            </FormItem>
          </Col>
        </Row>
        <Row>
          <Col span={6} offset={1}>
            <FormItem label="来源系统">
              {getFieldDecorator('sourceSystem', {
                initialValue: period ? period.sourceSystem : '',
              })(<Input disabled={true} />)}
            </FormItem>
          </Col>
          <Col span={6} offset={1}>
            <FormItem label="数据产生日期">
              {getFieldDecorator('sysDateTime', {
                initialValue: moment(period ? period.sysDateTime : '', dateFormat),
              })(<DatePicker disabled={true} />)}
            </FormItem>
          </Col>
          <Col span={6} offset={1}>
            <FormItem label="创建日期">
              {getFieldDecorator('createdDate', {
                initialValue: moment(period ? period.sysDateTime : '', dateFormat),
              })(<DatePicker disabled={true} />)}
            </FormItem>
          </Col>
        </Row>
        <div className="slide-footer">
          {/* <Button type="primary" htmlType="submit" loading={loading}>{this.$t('common.save')}</Button> */}
          <Button
            onClick={() => {
              this.props.onClose();
            }}
          >
            {this.$t('common.cancel')}
          </Button>

          <Button
            onClick={() => {
              this.props.lastPage();
            }}
          >
            {this.$t('common.last.one')}
          </Button>
          <Button
            onClick={() => {
              this.props.nextPage();
            }}
          >
            {this.$t('common.next.one')}
          </Button>
        </div>
      </Form>
    );
  }
}
function mapStateToProps(state) {
  return {};
}
const WrappedTaxVatTranInterface = Form.create()(TaxVatTranInterface);
export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(WrappedTaxVatTranInterface);

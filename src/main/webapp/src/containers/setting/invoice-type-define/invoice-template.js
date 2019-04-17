import React, { Component } from 'react';
import { Row, Col, Checkbox, Form, Button, Divider, message } from 'antd';
import service from './invoice-type-define-service.js';

const FormItem = Form.Item;
const CheckboxGroup = Checkbox.Group;

class InvoiceTemplate extends Component {
  constructor(props) {
    super(props);
    this.state = {
      group: {
        invoiceTypeMouldHeadColumn: { ...this.props.params.invoiceTypeMouldHeadColumn },
        invoiceTypeMouldLineColumn: { ...this.props.params.invoiceTypeMouldLineColumn },
      },
      //用于判定模板初始数据是否disabled-副本
      groupFb: {
        invoiceTypeMouldHeadColumn: { ...this.props.params.invoiceTypeMouldHeadColumn },
        invoiceTypeMouldLineColumn: { ...this.props.params.invoiceTypeMouldLineColumn },
      },
      templateId: this.props.templateId,
      templateTitle: this.props.templateTitle,
      saveLoading: false,
    };
  }

  componentWillReceiveProps = nextProps => {
    this.setState({
      group: {
        invoiceTypeMouldHeadColumn: { ...nextProps.params.invoiceTypeMouldHeadColumn },
        invoiceTypeMouldLineColumn: { ...nextProps.params.invoiceTypeMouldLineColumn },
      },
      groupFb: {
        invoiceTypeMouldHeadColumn: { ...nextProps.params.invoiceTypeMouldHeadColumn },
        invoiceTypeMouldLineColumn: { ...nextProps.params.invoiceTypeMouldLineColumn },
      },
      templateId: nextProps.templateId,
      templateTitle: nextProps.templateTitle,
    });
  };

  //保存
  handleTemplateOk = e => {
    e.preventDefault();
    const { group, templateId } = this.state;
    this.setState({ saveLoading: true });
    let params = {
      invoiceTypeMouldHeadColumn: {
        invoiceTypeId: templateId,
      },
      invoiceTypeMouldLineColumn: {
        invoiceTypeId: templateId,
      },
    };

    for (let key in group) {
      for (let item in group[key]) {
        if (item !== 'id') {
          params[key][item] =
            group[key][item].join().indexOf('REQUIRED') !== -1
              ? 'REQUIRED'
              : group[key][item].join().indexOf('ENABLED') !== -1
                ? 'ENABLED'
                : 'DISABLED';
        } else if (item == 'id') {
          params[key].id = group[key].id;
        }
      }
    }
    // console.log(params);
    service
      .updateInvoiceTemplate(params)
      .then(res => {
        this.setState({ saveLoading: false });
        message.success(this.$t('expense.edit.success')); /*编辑成功*/
        this.props.onClose();
      })
      .catch(err => {
        this.setState({ saveLoading: false });
        message.error(err.response.data.message);
      });
  };

  //关闭模板模态框
  handleCancel = () => {
    this.props.onClose();
  };

  //勾选checkbox后的模板
  handleChange = (value, dataIndex, flag) => {
    const { group } = this.state;

    if (value.join().indexOf('REQUIRED') !== -1) {
      if (flag == 'head') {
        group.invoiceTypeMouldHeadColumn[dataIndex] = ['REQUIRED', 'ENABLED'];
      } else if (flag == 'line') {
        group.invoiceTypeMouldLineColumn[dataIndex] = ['REQUIRED', 'ENABLED'];
      }
    } else {
      if (flag == 'head') {
        group.invoiceTypeMouldHeadColumn[dataIndex] = value;
      } else if (flag == 'line') {
        group.invoiceTypeMouldLineColumn[dataIndex] = value;
      }
    }

    this.setState({ group });
  };

  //disabled true or false 下的相关样式
  disabledOrNot = (groupFb, checkValue, dataIndex, flag) => {
    if (flag == 'head') {
      if (
        groupFb.invoiceTypeMouldHeadColumn &&
        groupFb.invoiceTypeMouldHeadColumn[dataIndex] &&
        groupFb.invoiceTypeMouldHeadColumn[dataIndex].join().indexOf(checkValue) !== -1
      ) {
        return { disabled: true, style: { color: '#000' } };
      } else return { disabled: false, style: { color: '#777' } };
    } else if (flag == 'line') {
      if (
        groupFb.invoiceTypeMouldLineColumn &&
        groupFb.invoiceTypeMouldLineColumn[dataIndex] &&
        groupFb.invoiceTypeMouldLineColumn[dataIndex].join().indexOf(checkValue) !== -1
      ) {
        return { disabled: true, style: { color: '#000' } };
      } else return { disabled: false, style: { color: '#777' } };
    }
  };

  render() {
    const { saveLoading, group, groupFb, templateTitle } = this.state;

    const formItemLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 10 },
    };

    const formItemLayoutCopyOne = {
      labelCol: { span: 9 },
      wrapperCol: { span: 15 },
    };

    const formItemLayoutCopyTwo = {
      labelCol: { span: 8 },
      wrapperCol: { span: 14 },
    };

    const formItemLayoutCopyThr = {
      labelCol: { span: 24 },
      wrapperCol: { span: 24 },
    };

    const paddingLeft = { paddingLeft: '20px' };

    const tableStyle = {
      backgroundColor: '#f7f7f7',
      border: '1px solid #dedede',
      borderRadius: '5px',
      padding: '10px 0 0 10px',
    };

    return (
      <div>
        <Form>
          <Row>
            <Col span={12}>
              <Row>
                <FormItem
                  {...formItemLayout}
                  label={this.$t('expense.wallet.invoicedate')}
                  style={{ marginBottom: 0 }}
                >
                  {/*开票日期*/}
                  <CheckboxGroup
                    style={paddingLeft}
                    value={
                      group.invoiceTypeMouldHeadColumn &&
                      group.invoiceTypeMouldHeadColumn.invoiceDate
                    }
                    onChange={value => this.handleChange(value, 'invoiceDate', 'head')}
                  >
                    <Checkbox
                      value="ENABLED"
                      disabled={
                        this.disabledOrNot(groupFb, 'ENABLED', 'invoiceDate', 'head').disabled
                      }
                    >
                      <span
                        style={this.disabledOrNot(groupFb, 'ENABLED', 'invoiceDate', 'head').style}
                      >
                        {this.$t('expense.according.to')}
                      </span>
                      {/*显示*/}
                    </Checkbox>
                    <Checkbox
                      value="REQUIRED"
                      disabled={
                        this.disabledOrNot(groupFb, 'REQUIRED', 'invoiceDate', 'head').disabled
                      }
                    >
                      <span
                        style={this.disabledOrNot(groupFb, 'REQUIRED', 'invoiceDate', 'head').style}
                      >
                        {this.$t('expense.mandatory')}
                      </span>
                      {/*必填*/}
                    </Checkbox>
                  </CheckboxGroup>
                </FormItem>
                <FormItem
                  {...formItemLayout}
                  label={this.$t('expense.wallet.invoicecode')}
                  style={{ marginBottom: 0 }}
                >
                  {/*发票代码*/}
                  <CheckboxGroup
                    style={paddingLeft}
                    value={
                      group.invoiceTypeMouldHeadColumn &&
                      group.invoiceTypeMouldHeadColumn.invoiceCode
                    }
                    onChange={value => this.handleChange(value, 'invoiceCode', 'head')}
                  >
                    <Checkbox
                      value="ENABLED"
                      disabled={
                        this.disabledOrNot(groupFb, 'ENABLED', 'invoiceCode', 'head').disabled
                      }
                    >
                      <span
                        style={this.disabledOrNot(groupFb, 'ENABLED', 'invoiceCode', 'head').style}
                      >
                        {this.$t('expense.according.to')}
                      </span>
                      {/*显示*/}
                    </Checkbox>
                    <Checkbox
                      value="REQUIRED"
                      disabled={
                        this.disabledOrNot(groupFb, 'REQUIRED', 'invoiceCode', 'head').disabled
                      }
                    >
                      <span
                        style={this.disabledOrNot(groupFb, 'REQUIRED', 'invoiceCode', 'head').style}
                      >
                        {this.$t('expense.mandatory')}
                      </span>
                      {/*必填*/}
                    </Checkbox>
                  </CheckboxGroup>
                </FormItem>
                <FormItem
                  {...formItemLayout}
                  label={this.$t('expense.desc.code2')}
                  style={{ marginBottom: 0 }}
                >
                  <CheckboxGroup
                    style={paddingLeft}
                    value={
                      group.invoiceTypeMouldHeadColumn && group.invoiceTypeMouldHeadColumn.checkCode
                    }
                    onChange={value => this.handleChange(value, 'checkCode', 'head')}
                  >
                    <Checkbox
                      value="ENABLED"
                      disabled={
                        templateTitle == 'N' &&
                        this.disabledOrNot(groupFb, 'ENABLED', 'checkCode', 'head').disabled
                      }
                    >
                      <span
                        style={this.disabledOrNot(groupFb, 'ENABLED', 'checkCode', 'head').style}
                      >
                        {this.$t('expense.according.to')}
                      </span>
                      {/*显示*/}
                    </Checkbox>
                    <Checkbox
                      value="REQUIRED"
                      disabled={
                        templateTitle == 'N' &&
                        this.disabledOrNot(groupFb, 'REQUIRED', 'checkCode', 'head').disabled
                      }
                    >
                      <span
                        style={this.disabledOrNot(groupFb, 'REQUIRED', 'checkCode', 'head').style}
                      >
                        {this.$t('expense.mandatory')}
                      </span>
                      {/*必填*/}
                    </Checkbox>
                  </CheckboxGroup>
                </FormItem>
              </Row>
            </Col>
            <Col span={12}>
              <Row>
                <FormItem
                  {...formItemLayout}
                  label={this.$t('expense.wallet.invoiceno')}
                  style={{ marginBottom: 0 }}
                >
                  {/*发票号码*/}
                  <CheckboxGroup
                    style={paddingLeft}
                    value={
                      group.invoiceTypeMouldHeadColumn && group.invoiceTypeMouldHeadColumn.invoiceNo
                    }
                    onChange={value => this.handleChange(value, 'invoiceNo', 'head')}
                  >
                    <Checkbox
                      value="ENABLED"
                      disabled={
                        this.disabledOrNot(groupFb, 'ENABLED', 'invoiceNo', 'head').disabled
                      }
                    >
                      <span
                        style={this.disabledOrNot(groupFb, 'ENABLED', 'invoiceNo', 'head').style}
                      >
                        {this.$t('expense.according.to')}
                      </span>
                      {/*显示*/}
                    </Checkbox>
                    <Checkbox
                      value="REQUIRED"
                      disabled={
                        this.disabledOrNot(groupFb, 'REQUIRED', 'invoiceNo', 'head').disabled
                      }
                    >
                      <span
                        style={this.disabledOrNot(groupFb, 'REQUIRED', 'invoiceNo', 'head').style}
                      >
                        {this.$t('expense.mandatory')}
                      </span>
                      {/*必填*/}
                    </Checkbox>
                  </CheckboxGroup>
                </FormItem>
                <FormItem
                  {...formItemLayout}
                  label={this.$t('expense.equipment.serial.number')}
                  style={{ marginBottom: 0 }}
                >
                  {/*设备编号*/}
                  <CheckboxGroup
                    style={paddingLeft}
                    value={
                      group.invoiceTypeMouldHeadColumn && group.invoiceTypeMouldHeadColumn.machineNo
                    }
                    onChange={value => this.handleChange(value, 'machineNo', 'head')}
                  >
                    <Checkbox value="ENABLED">
                      <span
                        style={this.disabledOrNot(groupFb, 'ENABLED', 'machineNo', 'head').style}
                      >
                        {this.$t('expense.according.to')}
                      </span>
                      {/*显示*/}
                    </Checkbox>
                    <Checkbox value="REQUIRED">
                      <span
                        style={this.disabledOrNot(groupFb, 'REQUIRED', 'machineNo', 'head').style}
                      >
                        {this.$t('expense.mandatory')}
                      </span>
                      {/*必填*/}
                    </Checkbox>
                  </CheckboxGroup>
                </FormItem>
                <FormItem
                  {...formItemLayout}
                  label={this.$t('expense.policy.currencyname')}
                  style={{ marginBottom: 0 }}
                >
                  {/*币种*/}
                  <CheckboxGroup
                    style={paddingLeft}
                    value={
                      group.invoiceTypeMouldHeadColumn &&
                      group.invoiceTypeMouldHeadColumn.currencyCode
                    }
                    onChange={value => this.handleChange(value, 'currencyCode', 'head')}
                  >
                    <Checkbox
                      value="ENABLED"
                      disabled={
                        this.disabledOrNot(groupFb, 'ENABLED', 'currencyCode', 'head').disabled
                      }
                    >
                      <span
                        style={this.disabledOrNot(groupFb, 'ENABLED', 'currencyCode', 'head').style}
                      >
                        {this.$t('expense.according.to')}
                      </span>
                      {/*显示*/}
                    </Checkbox>
                    <Checkbox
                      value="REQUIRED"
                      disabled={
                        this.disabledOrNot(groupFb, 'REQUIRED', 'currencyCode', 'head').disabled
                      }
                    >
                      <span
                        style={
                          this.disabledOrNot(groupFb, 'REQUIRED', 'currencyCode', 'head').style
                        }
                      >
                        {this.$t('expense.mandatory')}
                      </span>
                      {/*必填*/}
                    </Checkbox>
                  </CheckboxGroup>
                </FormItem>
              </Row>
            </Col>
          </Row>
          <Row>
            <Col span={8}>
              <FormItem
                {...formItemLayoutCopyOne}
                label={this.$t('expense.wallet.totalamount')}
                style={{ marginBottom: 0 }}
              >
                {/*价税合计*/}
                <CheckboxGroup
                  style={paddingLeft}
                  value={
                    group.invoiceTypeMouldHeadColumn && group.invoiceTypeMouldHeadColumn.totalAmount
                  }
                  onChange={value => this.handleChange(value, 'totalAmount', 'head')}
                >
                  <Checkbox value="ENABLED">
                    <span
                      style={this.disabledOrNot(groupFb, 'ENABLED', 'totalAmount', 'head').style}
                    >
                      {this.$t('expense.according.to')}
                    </span>
                    {/*显示*/}
                  </Checkbox>
                  <Checkbox value="REQUIRED">
                    <span
                      style={this.disabledOrNot(groupFb, 'REQUIRED', 'totalAmount', 'head').style}
                    >
                      {this.$t('expense.mandatory')}
                    </span>
                    {/*必填*/}
                  </Checkbox>
                </CheckboxGroup>
              </FormItem>
            </Col>
            <Col span={8}>
              <FormItem
                {...formItemLayoutCopyTwo}
                label={this.$t('expense.wallet.invoiceamount')}
                style={{ marginBottom: 0 }}
              >
                {/*金额合计*/}
                <CheckboxGroup
                  style={paddingLeft}
                  value={
                    group.invoiceTypeMouldHeadColumn &&
                    group.invoiceTypeMouldHeadColumn.invoiceAmount
                  }
                  onChange={value => this.handleChange(value, 'invoiceAmount', 'head')}
                >
                  <Checkbox value="ENABLED">
                    <span
                      style={this.disabledOrNot(groupFb, 'ENABLED', 'invoiceAmount', 'head').style}
                    >
                      {this.$t('expense.according.to')}
                    </span>
                    {/*显示*/}
                  </Checkbox>
                </CheckboxGroup>
              </FormItem>
            </Col>
            <Col span={8}>
              <FormItem
                {...formItemLayoutCopyTwo}
                label={this.$t('expense.total.amount.of')}
                style={{ marginBottom: 0 }}
              >
                {/*税额合计*/}
                <CheckboxGroup
                  style={paddingLeft}
                  value={
                    group.invoiceTypeMouldHeadColumn &&
                    group.invoiceTypeMouldHeadColumn.taxTotalAmount
                  }
                  onChange={value => this.handleChange(value, 'taxTotalAmount', 'head')}
                >
                  <Checkbox value="ENABLED">
                    <span
                      style={this.disabledOrNot(groupFb, 'ENABLED', 'taxTotalAmount', 'head').style}
                    >
                      {this.$t('expense.according.to')}
                    </span>
                    {/*显示*/}
                  </Checkbox>
                  <Checkbox value="REQUIRED">
                    <span
                      style={
                        this.disabledOrNot(groupFb, 'REQUIRED', 'taxTotalAmount', 'head').style
                      }
                    >
                      {this.$t('expense.mandatory')}
                    </span>
                    {/*必填*/}
                  </Checkbox>
                </CheckboxGroup>
              </FormItem>
            </Col>
          </Row>
          <Row>
            <Col span={12}>
              <FormItem
                {...formItemLayout}
                label={this.$t('expense.reverse.remark')}
                style={{ marginBottom: 0 }}
              >
                {/*备注*/}
                <CheckboxGroup
                  style={paddingLeft}
                  value={
                    group.invoiceTypeMouldHeadColumn && group.invoiceTypeMouldHeadColumn.remark
                  }
                  onChange={value => this.handleChange(value, 'remark', 'head')}
                >
                  <Checkbox value="ENABLED">
                    <span style={this.disabledOrNot(groupFb, 'ENABLED', 'remark', 'head').style}>
                      {this.$t('expense.according.to')}
                    </span>
                    {/*显示*/}
                  </Checkbox>
                  <Checkbox value="REQUIRED">
                    <span style={this.disabledOrNot(groupFb, 'REQUIRED', 'remark', 'head').style}>
                      {this.$t('expense.mandatory')}
                    </span>
                    {/*必填*/}
                  </Checkbox>
                </CheckboxGroup>
              </FormItem>
            </Col>
          </Row>
          <Row>
            <Col>
              <div style={tableStyle}>
                <Row>
                  <Col span={7}>
                    <Row>
                      <Col span={5}>
                        <FormItem
                          {...formItemLayoutCopyThr}
                          label={this.$t('expense.the.serial.number')} /*序号*/
                          style={{ marginBottom: 0 }}
                        >
                          <p style={{ margin: '0', textIndent: '10px' }}>1</p>
                        </FormItem>
                      </Col>
                      <Col span={19}>
                        <FormItem
                          {...formItemLayoutCopyThr}
                          label={this.$t('expense.desc.code3')}
                          style={{ marginBottom: 0, padding: '0 5px' }}
                        >
                          <CheckboxGroup
                            value={
                              group.invoiceTypeMouldLineColumn &&
                              group.invoiceTypeMouldLineColumn.goodsName
                            }
                            onChange={value => this.handleChange(value, 'goodsName', 'line')}
                          >
                            <Checkbox value="ENABLED">
                              <span
                                style={
                                  this.disabledOrNot(groupFb, 'ENABLED', 'goodsName', 'line').style
                                }
                              >
                                {this.$t('expense.according.to')}
                              </span>
                              {/*显示*/}
                            </Checkbox>
                            <Checkbox value="REQUIRED">
                              <span
                                style={
                                  this.disabledOrNot(groupFb, 'REQUIRED', 'goodsName', 'line').style
                                }
                              >
                                {this.$t('expense.mandatory')}
                              </span>
                              {/*必填*/}
                            </Checkbox>
                          </CheckboxGroup>
                        </FormItem>
                      </Col>
                    </Row>
                  </Col>
                  <Col span={17}>
                    <Row>
                      <Col span={3}>
                        <FormItem
                          {...formItemLayoutCopyThr}
                          label={this.$t('expense.specifications')} /*规格型号*/
                          style={{ marginBottom: 0, padding: '0 5px' }}
                        >
                          <CheckboxGroup
                            value={
                              group.invoiceTypeMouldLineColumn &&
                              group.invoiceTypeMouldLineColumn.specificationModel
                            }
                            onChange={value =>
                              this.handleChange(value, 'specificationModel', 'line')
                            }
                          >
                            <Checkbox value="ENABLED">
                              <span
                                style={
                                  this.disabledOrNot(
                                    groupFb,
                                    'ENABLED',
                                    'specificationModel',
                                    'line'
                                  ).style
                                }
                              >
                                {this.$t('expense.according.to')}
                              </span>
                              {/*显示*/}
                            </Checkbox>
                            <Checkbox value="REQUIRED" style={{ marginLeft: '0' }}>
                              <span
                                style={
                                  this.disabledOrNot(
                                    groupFb,
                                    'REQUIRED',
                                    'specificationModel',
                                    'line'
                                  ).style
                                }
                              >
                                {this.$t('expense.mandatory')}
                              </span>
                              {/*必填*/}
                            </Checkbox>
                          </CheckboxGroup>
                        </FormItem>
                      </Col>
                      <Col span={3}>
                        <FormItem
                          {...formItemLayoutCopyThr}
                          label={this.$t('expense.unit')} /*单位*/
                          style={{ marginBottom: 0, padding: '0 5px' }}
                        >
                          <CheckboxGroup
                            value={
                              group.invoiceTypeMouldLineColumn &&
                              group.invoiceTypeMouldLineColumn.unit
                            }
                            onChange={value => this.handleChange(value, 'unit', 'line')}
                          >
                            <Checkbox value="ENABLED">
                              <span
                                style={this.disabledOrNot(groupFb, 'ENABLED', 'unit', 'line').style}
                              >
                                {this.$t('expense.according.to')}
                              </span>
                              {/*显示*/}
                            </Checkbox>
                            <Checkbox value="REQUIRED" style={{ marginLeft: '0' }}>
                              <span
                                style={
                                  this.disabledOrNot(groupFb, 'REQUIRED', 'unit', 'line').style
                                }
                              >
                                {this.$t('expense.mandatory')}
                              </span>
                              {/*必填*/}
                            </Checkbox>
                          </CheckboxGroup>
                        </FormItem>
                      </Col>
                      <Col span={3}>
                        <FormItem
                          {...formItemLayoutCopyThr}
                          label={this.$t('expense.the.number.of')} /*数量*/
                          style={{ marginBottom: 0, padding: '0 5px' }}
                        >
                          <CheckboxGroup
                            value={
                              group.invoiceTypeMouldLineColumn &&
                              group.invoiceTypeMouldLineColumn.num
                            }
                            onChange={value => this.handleChange(value, 'num', 'line')}
                          >
                            <Checkbox value="ENABLED">
                              <span
                                style={this.disabledOrNot(groupFb, 'ENABLED', 'num', 'line').style}
                              >
                                {this.$t('expense.according.to')}
                              </span>
                              {/*显示*/}
                            </Checkbox>
                            <Checkbox value="REQUIRED" style={{ marginLeft: '0' }}>
                              <span
                                style={this.disabledOrNot(groupFb, 'REQUIRED', 'num', 'line').style}
                              >
                                {this.$t('expense.mandatory')}
                              </span>
                              {/*必填*/}
                            </Checkbox>
                          </CheckboxGroup>
                        </FormItem>
                      </Col>
                      <Col span={3}>
                        <FormItem
                          {...formItemLayoutCopyThr}
                          label={this.$t('expense.the.unit.price')} /*单价*/
                          style={{ marginBottom: 0, padding: '0 5px' }}
                        >
                          <CheckboxGroup
                            value={
                              group.invoiceTypeMouldLineColumn &&
                              group.invoiceTypeMouldLineColumn.unitPrice
                            }
                            onChange={value => this.handleChange(value, 'unitPrice', 'line')}
                          >
                            <Checkbox value="ENABLED">
                              <span
                                style={
                                  this.disabledOrNot(groupFb, 'ENABLED', 'unitPrice', 'line').style
                                }
                              >
                                {this.$t('expense.according.to')}
                              </span>
                              {/*显示*/}
                            </Checkbox>
                            <Checkbox value="REQUIRED" style={{ marginLeft: '0' }}>
                              <span
                                style={
                                  this.disabledOrNot(groupFb, 'REQUIRED', 'unitPrice', 'line').style
                                }
                              >
                                {this.$t('expense.mandatory')}
                              </span>
                              {/*必填*/}
                            </Checkbox>
                          </CheckboxGroup>
                        </FormItem>
                      </Col>
                      <Col span={3}>
                        <FormItem
                          {...formItemLayoutCopyThr}
                          label={this.$t('expense.amount')} /*金额*/
                          style={{ marginBottom: 0, padding: '0 5px' }}
                        >
                          <CheckboxGroup
                            value={
                              group.invoiceTypeMouldLineColumn &&
                              group.invoiceTypeMouldLineColumn.detailAmount
                            }
                            onChange={value => this.handleChange(value, 'detailAmount', 'line')}
                          >
                            <Checkbox
                              value="ENABLED"
                              disabled={
                                this.disabledOrNot(groupFb, 'ENABLED', 'detailAmount', 'line')
                                  .disabled
                              }
                            >
                              <span
                                style={
                                  this.disabledOrNot(groupFb, 'ENABLED', 'detailAmount', 'line')
                                    .style
                                }
                              >
                                {this.$t('expense.according.to')}
                              </span>
                              {/*显示*/}
                            </Checkbox>
                            <Checkbox
                              value="REQUIRED"
                              disabled={
                                this.disabledOrNot(groupFb, 'REQUIRED', 'detailAmount', 'line')
                                  .disabled
                              }
                              style={{ marginLeft: '0' }}
                            >
                              <span
                                style={
                                  this.disabledOrNot(groupFb, 'REQUIRED', 'detailAmount', 'line')
                                    .style
                                }
                              >
                                {this.$t('expense.mandatory')}
                              </span>
                              {/*必填*/}
                            </Checkbox>
                          </CheckboxGroup>
                        </FormItem>
                      </Col>
                      <Col span={3}>
                        <FormItem
                          {...formItemLayoutCopyThr}
                          label={this.$t('expense.rate')} /*税率*/
                          style={{ marginBottom: 0, padding: '0 5px' }}
                        >
                          <CheckboxGroup
                            value={
                              group.invoiceTypeMouldLineColumn &&
                              group.invoiceTypeMouldLineColumn.taxRate
                            }
                            onChange={value => this.handleChange(value, 'taxRate', 'line')}
                          >
                            <Checkbox
                              value="ENABLED"
                              disabled={
                                templateTitle !== 'N' &&
                                this.disabledOrNot(groupFb, 'ENABLED', 'taxRate', 'line').disabled
                              }
                            >
                              <span
                                style={
                                  this.disabledOrNot(groupFb, 'ENABLED', 'taxRate', 'line').style
                                }
                              >
                                {this.$t('expense.according.to')}
                              </span>
                              {/*显示*/}
                            </Checkbox>
                            <Checkbox
                              value="REQUIRED"
                              disabled={
                                templateTitle !== 'N' &&
                                this.disabledOrNot(groupFb, 'REQUIRED', 'taxRate', 'line').disabled
                              }
                              style={{ marginLeft: '0' }}
                            >
                              <span
                                style={
                                  this.disabledOrNot(groupFb, 'REQUIRED', 'taxRate', 'line').style
                                }
                              >
                                {this.$t('expense.mandatory')}
                              </span>
                              {/*必填*/}
                            </Checkbox>
                          </CheckboxGroup>
                        </FormItem>
                      </Col>
                      <Col span={3}>
                        <FormItem
                          {...formItemLayoutCopyThr}
                          label={this.$t('expense.tax')} /*税额*/
                          style={{ marginBottom: 0, padding: '0 5px' }}
                        >
                          <CheckboxGroup
                            value={
                              group.invoiceTypeMouldLineColumn &&
                              group.invoiceTypeMouldLineColumn.taxAmount
                            }
                            onChange={value => this.handleChange(value, 'taxAmount', 'line')}
                          >
                            <Checkbox
                              value="ENABLED"
                              disabled={
                                templateTitle !== 'N' &&
                                this.disabledOrNot(groupFb, 'ENABLED', 'taxAmount', 'line').disabled
                              }
                            >
                              <span
                                style={
                                  this.disabledOrNot(groupFb, 'ENABLED', 'taxAmount', 'line').style
                                }
                              >
                                {this.$t('expense.according.to')}
                              </span>
                              {/*显示*/}
                            </Checkbox>
                            <Checkbox
                              value="REQUIRED"
                              disabled={
                                templateTitle !== 'N' &&
                                this.disabledOrNot(groupFb, 'REQUIRED', 'taxAmount', 'line')
                                  .disabled
                              }
                              style={{ marginLeft: '0' }}
                            >
                              <span
                                style={
                                  this.disabledOrNot(groupFb, 'REQUIRED', 'taxAmount', 'line').style
                                }
                              >
                                {this.$t('expense.mandatory')}
                              </span>
                              {/*必填*/}
                            </Checkbox>
                          </CheckboxGroup>
                        </FormItem>
                      </Col>
                    </Row>
                  </Col>
                </Row>
              </div>
            </Col>
          </Row>
          <Row>
            <Col span={12}>
              <Row>
                <FormItem
                  {...formItemLayout}
                  label={this.$t('expense.the.purchaser')}
                  style={{ marginBottom: 0 }}
                >
                  {/*购买方*/}
                  <CheckboxGroup
                    style={paddingLeft}
                    value={
                      group.invoiceTypeMouldHeadColumn && group.invoiceTypeMouldHeadColumn.buyerName
                    }
                    onChange={value => this.handleChange(value, 'buyerName', 'head')}
                  >
                    <Checkbox value="ENABLED">
                      <span
                        style={this.disabledOrNot(groupFb, 'ENABLED', 'buyerName', 'head').style}
                      >
                        {this.$t('expense.according.to')}
                      </span>
                      {/*显示*/}
                    </Checkbox>
                    <Checkbox value="REQUIRED">
                      <span
                        style={this.disabledOrNot(groupFb, 'REQUIRED', 'buyerName', 'head').style}
                      >
                        {this.$t('expense.mandatory')}
                      </span>
                      {/*必填*/}
                    </Checkbox>
                  </CheckboxGroup>
                </FormItem>
                <FormItem
                  {...formItemLayout}
                  label={this.$t('expense.taxpayer.identification.number')}
                  style={{ marginBottom: 0 }}
                >
                  {/*纳税人识别号*/}
                  <CheckboxGroup
                    style={paddingLeft}
                    value={
                      group.invoiceTypeMouldHeadColumn &&
                      group.invoiceTypeMouldHeadColumn.buyerTaxNo
                    }
                    onChange={value => this.handleChange(value, 'buyerTaxNo', 'head')}
                  >
                    <Checkbox value="ENABLED">
                      <span
                        style={this.disabledOrNot(groupFb, 'ENABLED', 'buyerTaxNo', 'head').style}
                      >
                        {this.$t('expense.according.to')}
                      </span>
                      {/*显示*/}
                    </Checkbox>
                    <Checkbox value="REQUIRED">
                      <span
                        style={this.disabledOrNot(groupFb, 'REQUIRED', 'buyerTaxNo', 'head').style}
                      >
                        {this.$t('expense.mandatory')}
                      </span>
                      {/*必填*/}
                    </Checkbox>
                  </CheckboxGroup>
                </FormItem>
                <FormItem
                  {...formItemLayout}
                  label={this.$t('expense.address.phone')}
                  style={{ marginBottom: 0 }}
                >
                  <CheckboxGroup
                    style={paddingLeft}
                    value={
                      group.invoiceTypeMouldHeadColumn &&
                      group.invoiceTypeMouldHeadColumn.buyerAddPh
                    }
                    onChange={value => this.handleChange(value, 'buyerAddPh', 'head')}
                  >
                    <Checkbox value="ENABLED">
                      <span
                        style={this.disabledOrNot(groupFb, 'ENABLED', 'buyerAddPh', 'head').style}
                      >
                        {this.$t('expense.according.to')}
                      </span>
                      {/*显示*/}
                    </Checkbox>
                    <Checkbox value="REQUIRED">
                      <span
                        style={this.disabledOrNot(groupFb, 'REQUIRED', 'buyerAddPh', 'head').style}
                      >
                        {this.$t('expense.mandatory')}
                      </span>
                      {/*必填*/}
                    </Checkbox>
                  </CheckboxGroup>
                </FormItem>
                <FormItem
                  {...formItemLayout}
                  label={this.$t('expense.wallet.saleAccount')}
                  style={{ marginBottom: 0 }}
                >
                  <CheckboxGroup
                    style={paddingLeft}
                    value={
                      group.invoiceTypeMouldHeadColumn &&
                      group.invoiceTypeMouldHeadColumn.buyerAccount
                    }
                    onChange={value => this.handleChange(value, 'buyerAccount', 'head')}
                  >
                    <Checkbox value="ENABLED">
                      <span
                        style={this.disabledOrNot(groupFb, 'ENABLED', 'buyerAccount', 'head').style}
                      >
                        {this.$t('expense.according.to')}
                      </span>
                      {/*显示*/}
                    </Checkbox>
                    <Checkbox value="REQUIRED">
                      <span
                        style={
                          this.disabledOrNot(groupFb, 'REQUIRED', 'buyerAccount', 'head').style
                        }
                      >
                        {this.$t('expense.mandatory')}
                      </span>
                      {/*必填*/}
                    </Checkbox>
                  </CheckboxGroup>
                </FormItem>
              </Row>
            </Col>
          </Row>
          <Divider />
          <Row style={{ marginBottom: '30px' }}>
            <Col span={12}>
              <Row>
                <FormItem
                  {...formItemLayout}
                  label={this.$t('expense.the.sellers')}
                  style={{ marginBottom: 0 }}
                >
                  {/*销售方*/}
                  <CheckboxGroup
                    style={paddingLeft}
                    value={
                      group.invoiceTypeMouldHeadColumn && group.invoiceTypeMouldHeadColumn.salerName
                    }
                    onChange={value => this.handleChange(value, 'salerName', 'head')}
                  >
                    <Checkbox value="ENABLED">
                      <span
                        style={this.disabledOrNot(groupFb, 'ENABLED', 'salerName', 'head').style}
                      >
                        {this.$t('expense.according.to')}
                      </span>
                      {/*显示*/}
                    </Checkbox>
                    <Checkbox value="REQUIRED">
                      <span
                        style={this.disabledOrNot(groupFb, 'REQUIRED', 'salerName', 'head').style}
                      >
                        {this.$t('expense.mandatory')}
                      </span>
                      {/*必填*/}
                    </Checkbox>
                  </CheckboxGroup>
                </FormItem>
                <FormItem
                  {...formItemLayout}
                  label={this.$t('expense.taxpayer.identification.number')}
                  style={{ marginBottom: 0 }}
                >
                  {/*纳税人识别号*/}
                  <CheckboxGroup
                    style={paddingLeft}
                    value={
                      group.invoiceTypeMouldHeadColumn &&
                      group.invoiceTypeMouldHeadColumn.salerTaxNo
                    }
                    onChange={value => this.handleChange(value, 'salerTaxNo', 'head')}
                  >
                    <Checkbox value="ENABLED">
                      <span
                        style={this.disabledOrNot(groupFb, 'ENABLED', 'salerTaxNo', 'head').style}
                      >
                        {this.$t('expense.according.to')}
                      </span>
                      {/*显示*/}
                    </Checkbox>
                    <Checkbox value="REQUIRED">
                      <span
                        style={this.disabledOrNot(groupFb, 'REQUIRED', 'salerTaxNo', 'head').style}
                      >
                        {this.$t('expense.mandatory')}
                      </span>
                      {/*必填*/}
                    </Checkbox>
                  </CheckboxGroup>
                </FormItem>
                <FormItem
                  {...formItemLayout}
                  label={this.$t('expense.address.phone')}
                  style={{ marginBottom: 0 }}
                >
                  <CheckboxGroup
                    style={paddingLeft}
                    value={
                      group.invoiceTypeMouldHeadColumn &&
                      group.invoiceTypeMouldHeadColumn.salerAddPh
                    }
                    onChange={value => this.handleChange(value, 'salerAddPh', 'head')}
                  >
                    <Checkbox value="ENABLED">
                      <span
                        style={this.disabledOrNot(groupFb, 'ENABLED', 'salerAddPh', 'head').style}
                      >
                        {this.$t('expense.according.to')}
                      </span>
                      {/*显示*/}
                    </Checkbox>
                    <Checkbox value="REQUIRED">
                      <span
                        style={this.disabledOrNot(groupFb, 'REQUIRED', 'salerAddPh', 'head').style}
                      >
                        {this.$t('expense.mandatory')}
                      </span>
                      {/*必填*/}
                    </Checkbox>
                  </CheckboxGroup>
                </FormItem>
                <FormItem
                  {...formItemLayout}
                  label={this.$t('expense.wallet.saleAccount')}
                  style={{ marginBottom: 0 }}
                >
                  <CheckboxGroup
                    style={paddingLeft}
                    value={
                      group.invoiceTypeMouldHeadColumn &&
                      group.invoiceTypeMouldHeadColumn.salerAccount
                    }
                    onChange={value => this.handleChange(value, 'salerAccount', 'head')}
                  >
                    <Checkbox value="ENABLED">
                      <span
                        style={this.disabledOrNot(groupFb, 'ENABLED', 'salerAccount', 'head').style}
                      >
                        {this.$t('expense.according.to')}
                      </span>
                      {/*显示*/}
                    </Checkbox>
                    <Checkbox value="REQUIRED">
                      <span
                        style={
                          this.disabledOrNot(groupFb, 'REQUIRED', 'salerAccount', 'head').style
                        }
                      >
                        {this.$t('expense.mandatory')}
                      </span>
                      {/*必填*/}
                    </Checkbox>
                  </CheckboxGroup>
                </FormItem>
              </Row>
            </Col>
          </Row>
          <div className="slide-footer" style={{ textAlign: 'right' }}>
            <Button
              type="primary"
              htmlType="submit"
              loading={saveLoading}
              onClick={this.handleTemplateOk}
            >
              {this.$t('expense.determine')}
              {/*确 定*/}
            </Button>
            <Button type="primary" onClick={this.handleCancel} style={{ marginRight: '40px' }}>
              {this.$t('common.cancel')}
            </Button>
          </div>
        </Form>
      </div>
    );
  }
}

export default Form.create()(InvoiceTemplate);

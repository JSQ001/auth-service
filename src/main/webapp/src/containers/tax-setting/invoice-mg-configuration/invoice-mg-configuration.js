/*eslint-disable*/
import React from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { Form, Input, message, Collapse, Select, Col, Row, Button, InputNumber, Spin } from 'antd';
import InvoiceMgConfigurationService from './invoice-mg-configuration-service';

class InvoiceMgConfiguration extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      isDisabled: true,
      isMust: false,
      data: {},
      loading: true,
    };
  }

  componentDidMount() {
    this.getData();
  }
  getData() {
    const { taxpayerId } = this.props.match.params;
    // console.log(this.props.match.params.taxpayerId);
    this.setState({ loading: true });
    InvoiceMgConfigurationService.pageTaxVatInvoiceInfoByCond(taxpayerId)
      .then(response => {
        console.log(response.data);
        if (response.data !== '') {
          let dat = response.data[0];
          if (dat.elecEnableFlag == 0) {
            dat.elecEnableFlag = 'false';
          }
          if (dat.elecEnableFlag == 1) {
            dat.elecEnableFlag = 'true';
          }
          if (dat.inputEnabled == 0) {
            dat.inputEnabled = 'false';
          }
          if (dat.inputEnabled == 1) {
            dat.inputEnabled = 'true';
          }
          this.props.form.resetFields();
          this.setState({
            data: dat,
            loading: false,
          });
        }
      })
      .catch(e => {
        // if (e.response) {
        //     message.error(this.$t({ id: 'common.save.filed' }) + `,${e.response.data.message}`);
        // }
      });
  }
  handleCreate = () => {
    this.props.form.validateFieldsAndScroll((err, values) => {
      console.log(values);
      if (err) {
        this.setState({
          loading: false,
        });
        return;
      }
      //   console.log(values);
      if (!err) {
        console.log(values);
        const { data } = this.state;
        let params = {
          ...values,
          id: data.id,
          outputTaDiskFirm: values.outputTaDiskFirm,
          boardNumber: values.boardNumber,
          boardWord: values.boardWord,
          signPasswd: values.signPasswd,
          serverNumber: values.serverNumber,
          maximumLines: values.maximumLines,
          maximumAmount: values.maximumAmount,
          speMaximumAmount: values.speMaximumAmount,
          elecEnableFlag: values.elecEnableFlag === 'true' ? true : false,
          elecMaximumAmount: values.elecMaximumAmount,
          elecSpeMaximumAmount: values.elecSpeMaximumAmount,
          inputTaxDiskFirm: values.inputTaxDiskFirm,
          inputEnabled: values.inputEnabled === 'true' ? true : false,
          accessKeyUserName: values.accessKeyUserName,
          accessKeyPassword: values.accessKeyPassword,
          taxpayerId: this.props.match.params.taxpayerId,
        };
        console.log(params);
        const method = data.id
          ? InvoiceMgConfigurationService.updateTaxVatInvoiceInfo
          : InvoiceMgConfigurationService.insertTaxVatInvoiceInfo;
        method(params)
          .then(response => {
            message.success(
              this.$t({ id: 'common.operate.success' }, { name: values.description })
            );
            this.setState({
              loading: false,
            });
            // this.props.close(true);
            this.getData();
          })
          .catch(e => {
            if (e.response) {
              message.error(this.$t({ id: 'common.save.filed' }) + `,${e.response.data.message}`);
            }
          });
      }
    });
  };

  //返回纳税主体信息管理界面
  onBackClick = e => {
    e.preventDefault();
    this.props.dispatch(
      routerRedux.replace({
        pathname: `/basic-tax-information-management/tax-subject-allocation/tax-subject-allocation`,
      })
    );
  };

  render() {
    const { form } = this.props;
    const { getFieldDecorator } = form;
    const Option = Select.Option;
    const Panel = Collapse.Panel;
    const { isDisabled, isMust, data, loading } = this.state;
    const formItemLayout = {
      labelCol: {
        xs: { span: 10 },
        sm: { span: 10 },
      },
      wrapperCol: {
        xs: { span: 14 },
        sm: { span: 14 },
      },
    };
    return (
      <div>
        <Collapse defaultActiveKey={['1', '2']} onChange={this.callback}>
          <Panel header="销项端" key="1">
            <Row gutter={24}>
              <Col span={8}>
                <Form.Item {...formItemLayout} label="盘商">
                  {getFieldDecorator('outputTaxDiskFirm', {
                    rules: [{ required: true, message: this.$t('common.please.enter') }],
                    initialValue: data.outputTaxDiskFirm,
                  })(
                    <Select placeholder="请选择" style={{ width: '100%' }}>
                      <Option value="航信">航信</Option>
                      <Option value="百望">百望</Option>
                    </Select>
                  )}
                </Form.Item>
              </Col>
              <Col span={8}>
                <Form.Item {...formItemLayout} label="税控盘编号">
                  {getFieldDecorator('boardNumber', {
                    rules: [{ required: false }],
                    initialValue: data.boardNumber,
                  })(<Input placeholder="请输入" />)}
                </Form.Item>
              </Col>
              <Col span={8}>
                <Form.Item {...formItemLayout} label="税控盘口令">
                  {getFieldDecorator('boardWord', {
                    rules: [{ required: false }],
                    initialValue: data.boardWord,
                  })(<Input placeholder="请输入" />)}
                </Form.Item>
              </Col>
            </Row>
            <Row gutter={24}>
              <Col span={8}>
                <Form.Item {...formItemLayout} label="税务数据证书密码">
                  {getFieldDecorator('signPasswd', {
                    rules: [{ required: false, message: this.$t('common.please.enter') }],
                    initialValue: data.signPasswd,
                  })(<Input placeholder="请输入" />)}
                </Form.Item>
              </Col>
              <Col span={8}>
                <Form.Item {...formItemLayout} label="服务器编号">
                  {getFieldDecorator('serverNumber', {
                    rules: [{ required: false }],
                    initialValue: data.serverNumber,
                  })(<Input placeholder="请输入" />)}
                </Form.Item>
              </Col>
              <Col span={8}>
                <Form.Item {...formItemLayout} label="最大开票行数">
                  {getFieldDecorator('maximumLines', {
                    rules: [{ required: true, message: this.$t('common.please.enter') }],
                    initialValue: data.maximumLines,
                  })(
                    <Select placeholder="请选择" style={{ width: '100%' }}>
                      <Option value="1">1</Option>
                      <Option value="2">2</Option>
                      <Option value="3">3</Option>
                      <Option value="4">4</Option>
                      <Option value="5">5</Option>
                      <Option value="6">6</Option>
                      <Option value="7">7</Option>
                      <Option value="8">8</Option>
                    </Select>
                  )}
                </Form.Item>
              </Col>
            </Row>
            <Row gutter={24}>
              <Col span={8}>
                <Form.Item {...formItemLayout} label="最大开票限额(普票)">
                  {getFieldDecorator('maximumAmount', {
                    rules: [{ required: true, message: this.$t('common.please.enter') }],
                    initialValue: data.maximumAmount,
                  })(<InputNumber placeholder="请输入" style={{ width: '100%' }} />)}
                </Form.Item>
              </Col>
              <Col span={8}>
                <Form.Item {...formItemLayout} label="最大开票限额(专票)">
                  {getFieldDecorator('speMaximumAmount', {
                    rules: [{ required: true, message: this.$t('common.please.enter') }],
                    initialValue: data.maximumAmount,
                  })(<InputNumber placeholder="请输入" style={{ width: '100%' }} />)}
                </Form.Item>
              </Col>
              <Col span={8}>
                <Form.Item {...formItemLayout} label="是否启用电子发票">
                  {getFieldDecorator('elecEnableFlag', {
                    rules: [{ required: false }],
                    initialValue: data.elecEnableFlag || 'false',
                  })(
                    <Select
                      placeholder="请选择"
                      style={{ width: '100%' }}
                      onChange={value => {
                        if (value == 'true') {
                          this.setState({
                            isDisabled: false,
                            isMust: true,
                          });
                        }
                        if (value == 'false') {
                          this.setState({
                            isDisabled: true,
                            isMust: false,
                          });
                        }
                      }}
                    >
                      <Option value="true">是</Option>
                      <Option value="false">否</Option>
                    </Select>
                  )}
                </Form.Item>
              </Col>
            </Row>
            <Row gutter={24}>
              <Col span={8}>
                <Form.Item {...formItemLayout} label="最大开票限额(电子普票)">
                  {getFieldDecorator('elecMaximumAmount', {
                    rules: [{ required: isMust, message: this.$t('common.please.enter') }],
                    initialValue: data.elecMaximumAmount,
                  })(
                    <InputNumber
                      placeholder="请输入"
                      disabled={isDisabled}
                      style={{ width: '100%' }}
                    />
                  )}
                </Form.Item>
              </Col>
              <Col span={8}>
                <Form.Item
                  {...formItemLayout}
                  label="最大开票限额
                                (电子专票)"
                >
                  {getFieldDecorator('elecSpeMaximumAmount', {
                    rules: [{ required: isMust, message: this.$t('common.please.enter') }],
                    initialValue: data.elecSpeMaximumAmount,
                  })(
                    <InputNumber
                      placeholder="请输入"
                      disabled={isDisabled}
                      style={{ width: '100%' }}
                    />
                  )}
                </Form.Item>
              </Col>
            </Row>
          </Panel>
          <Panel header="进项端" key="2">
            <Row gutter={24}>
              <Col span={8}>
                <Form.Item {...formItemLayout} label="盘商">
                  {getFieldDecorator('inputTaxDiskFirm', {
                    rules: [{ required: true, message: this.$t('common.please.enter') }],
                    initialValue: data.inputTaxDiskFirm,
                  })(
                    <Select placeholder="请选择" style={{ width: '100%' }}>
                      <Option value="航信">航信</Option>
                      <Option value="百望">百望</Option>
                    </Select>
                  )}
                </Form.Item>
              </Col>
              <Col span={8}>
                <Form.Item {...formItemLayout} label="是否启用进项接口">
                  {getFieldDecorator('inputEnabled', {
                    rules: [{ required: false }],
                    initialValue: data.inputEnabled || 'false',
                  })(
                    <Select placeholder="请选择" style={{ width: '100%' }}>
                      <Option value="true">是</Option>
                      <Option value="false">否</Option>
                    </Select>
                  )}
                </Form.Item>
              </Col>
              <Col span={8}>
                <Form.Item {...formItemLayout} label="进项接口用户名">
                  {getFieldDecorator('accessKeyUserName', {
                    rules: [{ required: false }],
                    initialValue: data.accessKeyUserName,
                  })(<Input placeholder="请输入" />)}
                </Form.Item>
              </Col>
            </Row>
            <Row gutter={24}>
              <Col span={8}>
                <Form.Item {...formItemLayout} label="进项接口密码">
                  {getFieldDecorator('accessKeyPassword', {
                    rules: [{ required: false }],
                    initialValue: data.accessKeyPassword,
                  })(<Input placeholder="请输入" />)}
                </Form.Item>
              </Col>
            </Row>
          </Panel>
        </Collapse>
        <div style={{ marginTop: 20 }}>
          <Button type="primary" onClick={this.handleCreate}>
            保存
          </Button>
          <Button type={'primary'} style={{ marginLeft: 10 }}>
            <a onClick={this.onBackClick}>返回</a>
          </Button>
        </div>
      </div>
    );
  }
}
export default connect()(Form.create()(InvoiceMgConfiguration));
// export default Form.create()(TaxInformationMaintenance);

/*eslint-disable*/
import React from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import {
  Form,
  Input,
  message,
  Collapse,
  Select,
  Col,
  Row,
  Button,
  InputNumber,
  Tabs,
  Tooltip,
  Icon,
} from 'antd';
import Chooser from 'widget/chooser';
import TaxInformationMaintenanceService from './tax-information-maintenance-service';
class TaxInformationMaintenance extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      isDeclareLevel: false,
      isPreDeclarePeriod: false,
      isPreRate: false,
      isRemDeclarePeriod: false,
      // data: {
      //     declareMode: '',
      //     declareLevel: '',
      //     parentTaxpayerId: '',
      //     declarePeriod: '',
      //     preDeclarePeriod: '',
      //     preRate: '',
      //     preDistributionRatio: '',
      //     remDeclarePeriod: '',
      //     remDistributionRatio: ''
      // },
      data: {},
    };
  }
  componentDidMount() {
    console.log(this.props.match.params.taxpayerId);
    this.getData();
  }
  callback(key) {
    console.log(key);
  }
  getData() {
    const { taxpayerId } = this.props.match.params;
    TaxInformationMaintenanceService.pageTaxValueAddedTaxInfoByCond(taxpayerId)
      .then(response => {
        console.log(response.data);
        if (response.data !== '') {
          this.props.form.resetFields();
          this.setState({
            data: response.data.length ? response.data[0] : {},
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
          declareMode: values.declareMode,
          declareLevel: values.declareLevel,
          parentTaxpayerId: values.parentTaxpayerId && values.parentTaxpayerId[0].id,
          declarePeriod: values.declarePeriod,
          preDeclarePeriod: values.preDeclarePeriod,
          isPreRate: values.isPreRate,
          preDistributionRatio: values.preDistributionRatio,
          remDeclarePeriod: values.remDeclarePeriod,
          remDistributionRatio: values.remDistributionRatio,
          taxpayerId: this.props.match.params.taxpayerId,
        };
        console.log(params);
        const method = data.id
          ? TaxInformationMaintenanceService.updateTaxValueAddedTaxInfo
          : TaxInformationMaintenanceService.insertTaxValueAddedTaxInfo;
        method(params)
          .then(response => {
            message.success(
              this.$t({ id: 'common.operate.success' }, { name: values.description })
            );
            this.setState({
              loading: false,
              data: response.data,
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
    const Panel = Collapse.Panel;
    const TabPane = Tabs.TabPane;
    const Option = Select.Option;
    const { isDeclareLevel, isPreDeclarePeriod, isPreRate, isRemDeclarePeriod, data } = this.state;
    const formItemLayout = {
      labelCol: {
        xs: { span: 12 },
        sm: { span: 12 },
      },
      wrapperCol: {
        xs: { span: 12 },
        sm: { span: 12 },
      },
    };
    return (
      <div>
        <Tabs defaultActiveKey="1">
          <TabPane tab="增值税" key="1">
            <Form style={{ marginTop: 10 }}>
              <Collapse defaultActiveKey={['1', '2']} onChange={this.callback}>
                <Panel header="基础信息" key="1">
                  <Row gutter={24}>
                    <Col span={6}>
                      <Form.Item {...formItemLayout} label="申报模式">
                        {getFieldDecorator('declareMode', {
                          rules: [{ required: true, message: this.$t('common.please.enter') }],
                          initialValue: data.declareMode,
                        })(
                          <Select
                            placeholder="请选择"
                            onChange={value => {
                              if (value == '一般申报') {
                                this.setState({
                                  isDeclareLevel: true,
                                  isPreDeclarePeriod: false,
                                  isPreRate: false,
                                  isRemDeclarePeriod: false,
                                });
                              }
                              if (value == '总分机构汇总申报') {
                                this.setState({
                                  isDeclareLevel: false,
                                  isPreDeclarePeriod: true,
                                  isPreRate: true,
                                  isRemDeclarePeriod: true,
                                });
                              }
                            }}
                          >
                            <Option value="一般申报">一般申报</Option>
                            <Option value="总分机构汇总申报">总分机构汇总申报</Option>
                          </Select>
                        )}
                      </Form.Item>
                    </Col>
                    <Col span={6}>
                      <Form.Item {...formItemLayout} label="申报层级">
                        {getFieldDecorator('declareLevel', {
                          rules: [{ required: true, message: this.$t('common.please.enter') }],
                          initialValue: data.declareLevel,
                        })(
                          <Select placeholder="请选择">
                            <Option value="一级（省级）">一级（省级）</Option>
                            <Option value="二级（地市）">二级（地市）</Option>
                            <Option value="三级（区县）">三级（区县）</Option>
                          </Select>
                        )}
                      </Form.Item>
                    </Col>
                    <Col span={6}>
                      <Form.Item {...formItemLayout} label="上级纳税主体">
                        {getFieldDecorator('parentTaxpayerId', {
                          rules: [
                            {
                              required: false,
                              message: this.$t('common.please.enter'),
                            },
                          ],
                          initialValue: data.parentTaxpayerId && [
                            { id: data.parentTaxpayerId, taxpayerName: data.parentTaxpayerName },
                          ],
                          // initialValue: this.props.params.preferentialTaxRate || '',
                        })(
                          <Chooser
                            labelKey="taxpayerName"
                            valueKey="id"
                            type="parent_tax_payer"
                            single
                          />
                        )}
                      </Form.Item>
                    </Col>
                    <Col span={6}>
                      <Form.Item {...formItemLayout} label="申报周期">
                        {getFieldDecorator('declarePeriod', {
                          rules: [
                            { required: isDeclareLevel, message: this.$t('common.please.enter') },
                          ],
                          initialValue: data.declarePeriod,
                        })(
                          <Select placeholder="请选择">
                            <Option value="月度">月度</Option>
                            <Option value="季度">季度</Option>
                            <Option value="半年">半年</Option>
                            <Option value="年度">年度</Option>
                          </Select>
                        )}
                      </Form.Item>
                    </Col>
                  </Row>
                </Panel>
                {/* </Collapse>
                            <Collapse style={{ marginTop: 10 }}> */}
                <Panel header="汇缴信息" key="2">
                  <Row gutter={24}>
                    <Col span={6}>
                      <Form.Item {...formItemLayout} label="预缴申报周期">
                        {getFieldDecorator('preDeclarePeriod', {
                          rules: [
                            {
                              required: isPreDeclarePeriod,
                              message: this.$t('common.please.enter'),
                            },
                          ],
                          initialValue: data.preDeclarePeriod,
                        })(
                          <Select placeholder="请选择">
                            <Option value="月度">月度</Option>
                            <Option value="季度">季度</Option>
                            <Option value="半年">半年</Option>
                            <Option value="年度">年度</Option>
                          </Select>
                        )}
                      </Form.Item>
                    </Col>
                    <Col span={6}>
                      <Form.Item
                        {...formItemLayout}
                        label={
                          <span>
                            预征率&nbsp;
                            <Tooltip title="例30%">
                              <Icon type="question-circle-o" />
                            </Tooltip>
                          </span>
                        }
                      >
                        {getFieldDecorator('preRate', {
                          rules: [{ required: isPreRate, message: this.$t('common.please.enter') }],
                          initialValue: data.preRate,
                        })(
                          <InputNumber
                            placeholder="请输入"
                            style={{ width: '100%' }}
                            min="0"
                            formatter={value => `${value}%`}
                            parser={value => value.replace('%', '')}
                          />
                        )}
                      </Form.Item>
                    </Col>
                    <Col span={6}>
                      <Form.Item
                        {...formItemLayout}
                        label={
                          <span>
                            预缴分配比例&nbsp;
                            <Tooltip title="例32%">
                              <Icon type="question-circle-o" />
                            </Tooltip>
                          </span>
                        }
                      >
                        {getFieldDecorator('preDistributionRatio', {
                          rules: [{ required: false }],
                          initialValue: data.preDistributionRatio,
                        })(
                          <InputNumber
                            placeholder="请输入"
                            style={{ width: '100%' }}
                            formatter={value => `${value}%`}
                            min="0"
                            parser={value => value.replace('%', '')}
                          />
                        )}
                      </Form.Item>
                    </Col>
                    <Col span={6}>
                      <Form.Item {...formItemLayout} label="汇缴申报周期">
                        {getFieldDecorator('remDeclarePeriod', {
                          rules: [
                            {
                              required: isRemDeclarePeriod,
                              message: this.$t('common.please.enter'),
                            },
                          ],
                          initialValue: data.remDeclarePeriod,
                        })(
                          <Select placeholder="请选择">
                            <Option value="月度">月度</Option>
                            <Option value="季度">季度</Option>
                            <Option value="半年">半年</Option>
                            <Option value="年度">年度</Option>
                          </Select>
                        )}
                      </Form.Item>
                    </Col>
                  </Row>
                  <Row gutter={24}>
                    <Col span={6}>
                      <Form.Item
                        {...formItemLayout}
                        label={
                          <span>
                            汇缴分配比例&nbsp;
                            <Tooltip title="例42%">
                              <Icon type="question-circle-o" />
                            </Tooltip>
                          </span>
                        }
                      >
                        {getFieldDecorator('remDistributionRatio', {
                          rules: [{ required: false }],
                          initialValue: data.remDistributionRatio,
                        })(
                          <InputNumber
                            placeholder="请输入"
                            min="0"
                            style={{ width: '100%' }}
                            formatter={value => `${value}%`}
                            parser={value => value.replace('%', '')}
                          />
                        )}
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
            </Form>
          </TabPane>
        </Tabs>
      </div>
    );
  }
}
const WrappedInvoicingSite = Form.create()(TaxInformationMaintenance);
function mapStateToProps(state) {
  return {
    company: state.user.company,
  };
}
export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(WrappedInvoicingSite);

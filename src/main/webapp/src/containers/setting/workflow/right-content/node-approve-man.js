import React from 'react';
import { connect } from 'dva';
import { Form, Card, Input, Radio, Select, Button, message, Icon } from 'antd';
const FormItem = Form.Item;
const RadioGroup = Radio.Group;
const Option = Select.Option;
import PropTypes from 'prop-types';

import AddPersonModal from 'containers/setting/workflow/right-content/add-person-modal';
import workflowService from 'containers/setting/workflow/workflow.service';

class NodeApproveMan extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      ruleApprovalNodeOid: '',
      selectApprovalRule: 5005,
      showDepManager: false, //【包含申请人】是否选择部门经理
      formFieldList: null, //表单条件字段 字段类型(100默认, 101文本, 102整数, 103日期, 104浮点数, 105日期, 106值列表, 107GPS, 108布尔)
      formFieldCostCenterList: null, //审批条件为成本中心属性字段
      isRuleInEdit: false, //是否有审批条件处于编辑状态
    };
  }

  componentDidMount() {
    this.setState({
      ruleApprovalNodeOid: this.props.basicInfo.ruleApprovalNodeOid,
      selectApprovalRule: this.props.basicInfo.selfApprovalRule,
      showDepManager: !(
        this.props.basicInfo.selfApprovalRule === 5001 ||
        this.props.basicInfo.selfApprovalRule === 5002
      ),
    });
  }

  componentWillReceiveProps(nextProps) {
    if (this.state.ruleApprovalNodeOid !== nextProps.basicInfo.ruleApprovalNodeOid) {
      let selfApprovalRuleFlag =
        nextProps.basicInfo.selfApprovalRule === 5001 ||
        nextProps.basicInfo.selfApprovalRule === 5002;
      this.setState(
        {
          ruleApprovalNodeOid: nextProps.basicInfo.ruleApprovalNodeOid,
          selectApprovalRule: selfApprovalRuleFlag ? 5005 : nextProps.basicInfo.selfApprovalRule,
          showDepManager: !selfApprovalRuleFlag,
        },
        () => {
          this.props.form.resetFields();
        }
      );
    }
  }

  //保存基础信息
  handleSaveBasicInfo = e => {
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        const { basicInfo } = this.props;
        if (values.selfApprovalRule === 50035005) {
          values.selfApprovalRule = this.state.selectApprovalRule;
        }
        let params = values;
        params.approvalActions = basicInfo.approvalActions;
        params.code = basicInfo.code;
        params.name = basicInfo.name;
        params.notifyInfo = basicInfo.notifyInfo;
        params.repeatRule = basicInfo.repeatRule;
        params.ruleApprovalNodeOid = basicInfo.ruleApprovalNodeOid;
        params.status = basicInfo.status;
        params.type = basicInfo.type;
        this.setState({ loading: true });
        workflowService
          .modifyApprovalNodes(params)
          .then(() => {
            this.setState({ loading: false });
            this.props.basicInfoSaveHandle();
            message.success(this.$t('common.save.success', { name: '' }));
          })
          .catch(() => {
            this.setState({ loading: false });
          });
      }
    });
  };

  render() {
    const { getFieldDecorator } = this.props.form;
    const { basicInfo, formInfo, language } = this.props;
    const { loading, selectApprovalRule, showDepManager } = this.state;
    let selfApprovalRule = basicInfo.selfApprovalRule;
    selfApprovalRule =
      selfApprovalRule === 5001 || selfApprovalRule === 5002 ? selfApprovalRule : 50035005;
    const formItemLayout = {
      labelCol: { span: language.code === 'zh_cn' ? 4 : 6 },
      wrapperCol: { span: language.code === 'zh_cn' ? 20 : 18 },
    };
    return (
      <div className="node-approve-man">
        <Card className="basic-info-container">
          <h3 className="title">{this.$t('setting.key1371' /*基础信息*/)}</h3>
          <Form onSubmit={this.handleSaveBasicInfo}>
            <FormItem
              {...formItemLayout}
              label={this.$t('setting.key1372' /*节点名称*/)}
              colon={false}
            >
              {getFieldDecorator('remark', {
                rules: [
                  {
                    max: 8,
                    message: this.$t('common.max.characters.length', { max: 8 }),
                  },
                ],
                initialValue: basicInfo.remark,
              })(
                <Input
                  placeholder={this.$t('common.please.enter')}
                  style={{ width: 200 }}
                  autoComplete="off"
                />
              )}
            </FormItem>
            <FormItem
              {...formItemLayout}
              label={this.$t('setting.key1382' /*节点为空时*/)}
              colon={false}
            >
              {getFieldDecorator('nullableRule', {
                initialValue: basicInfo.nullableRule,
              })(
                <RadioGroup>
                  <Radio value={2001}>{this.$t('setting.key1383' /*跳过*/)}</Radio>
                  <Radio value={2002}>{this.$t('setting.key1384' /*不跳过*/)}</Radio>
                </RadioGroup>
              )}
            </FormItem>
            <FormItem
              {...formItemLayout}
              label={this.$t('setting.key1385' /*会签规则*/)}
              colon={false}
            >
              {getFieldDecorator('countersignRule', {
                initialValue: basicInfo.countersignRule,
              })(
                <RadioGroup>
                  <Radio value={3001}>{this.$t('setting.key1386' /*所有人审批通过*/)}</Radio>
                  <Radio value={3002}>{this.$t('setting.key1387' /*一票通过/一票否决*/)}</Radio>
                  <Radio value={3003}>{this.$t('setting.key1503' /*任一人*/)}</Radio>
                </RadioGroup>
              )}
            </FormItem>
            <FormItem
              {...formItemLayout}
              label={this.$t('setting.key1388' /*包含申请人*/)}
              colon={false}
            >
              {getFieldDecorator('selfApprovalRule', {
                initialValue: selfApprovalRule,
              })(
                <RadioGroup
                  onChange={e =>
                    this.setState({
                      showDepManager: e.target.value === 50035005,
                      selectApprovalRule: e.target.value === 50035005 ? 5005 : e.target.value,
                    })
                  }
                >
                  <Radio value={5002}>{this.$t('setting.key1384' /*不跳过*/)}</Radio>
                  <Radio value={5001}>{this.$t('setting.key1383' /*跳过*/)}</Radio>
                  {/* 5005 本级、5003上级 */}
                  <Radio value={50035005}>
                    {this.$t('setting.key1389' /*转交部门经理*/)}
                    {showDepManager && (
                      <Select
                        className="approval-select"
                        value={selectApprovalRule}
                        onChange={value => {
                          this.setState({ selectApprovalRule: value });
                        }}
                      >
                        <Option value={5005}>{this.$t('setting.key1390' /*本级*/)}</Option>
                        <Option value={5003}>{this.$t('setting.key1391' /*上级*/)}</Option>
                      </Select>
                    )}
                  </Radio>
                </RadioGroup>
              )}
            </FormItem>
            {/*报销单的formType以3开头，只有报销单才可以选择是否允许修改费用*/}
            {formInfo.formType &&
              String(formInfo.formType).charAt(0) === '3' && (
                <FormItem
                  {...formItemLayout}
                  label={this.$t('setting.key1392' /*允许修改费用*/)}
                  colon={false}
                >
                  {getFieldDecorator('invoiceAllowUpdateType', {
                    initialValue: basicInfo.invoiceAllowUpdateType || 0,
                  })(
                    <RadioGroup>
                      <Radio value={1}>{this.$t('setting.key1393' /*是*/)}</Radio>
                      <Radio value={0}>{this.$t('setting.key1394' /*否*/)}</Radio>
                    </RadioGroup>
                  )}
                </FormItem>
              )}
            <Button type="primary" htmlType="submit" loading={loading}>
              {this.$t('common.save')}
            </Button>
          </Form>
        </Card>
        <div className="add-btn-container">
          <Button type="primary" onClick={() => this.props.modalVisibleHandle(true)}>
            {this.$t('setting.key1395' /*添加审批人员*/)}
          </Button>
          <Icon type="exclamation-circle-o" className="approve-info-icon" />
          <span className="approve-info-text">
            {this.$t('setting.key1381' /*一个条件组内多条件为and关系, 不同条件组为or关系*/)}
          </span>
        </div>
      </div>
    );
  }
}

NodeApproveMan.propTypes = {
  basicInfo: PropTypes.object,
  formInfo: PropTypes.object,
  basicInfoSaveHandle: PropTypes.func, //基本信息保存成功的回调
  modalVisibleHandle: PropTypes.func, //用于添加审批人modal是否显示的传参
};

function mapStateToProps(state) {
  return {
    language: state.languages,
  };
}

const wrappedNodeApproveMan = Form.create()(NodeApproveMan);

export default connect(mapStateToProps)(wrappedNodeApproveMan);

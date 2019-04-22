import React from 'react';
import { connect } from 'dva';
import 'styles/fund/account.scss';
import {
  Form,
  Input,
  Button,
  Switch,
  message,
  Select,
  Icon,
  Modal,
  InputNumber,
  Row,
  Col,
} from 'antd';
import Table from 'widget/table';
import Chooser from 'widget/chooser';
import automaticPaymentRulesService from './automatic-payment-rules.service';

const FormItem = Form.Item;
const { Option } = Select;

class AutoPayRuleAdd extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      publicPrivateList: [], // 对公对私值列表
      systemSourceList: [], // 来源系统值列表
      loading: false,
      isNew: true, // 是否为新建
      selectValue: {}, // 选中madal中的行值
      visible: false,
      companyId: '', // 选择公司后的companyId，付款账号查询需要
      moneyFrom: '', // 金额从
      moneyTo: '', // 金额至
      creditFrom: '', // 信用分从
      creditTo: '', // 信用分至
      pagination: {
        total: 0,
        page: 0,
        pageSize: 10,
        current: 1,
        showSizeChanger: true,
        showQuickJumper: true,
        showTotal: (total, range) => `显示${range[0]}-${range[1]} 共 ${total} 条`,
      },
      columns: [
        {
          title: '银行账号',
          dataIndex: 'accountNumber',
          width: 100,
          align: 'center',
        },
        {
          title: '账号户名',
          dataIndex: 'accountName',
          width: 100,
          align: 'center',
        },
        {
          title: '开户银行',
          dataIndex: 'openBankName',
          width: 100,
          align: 'center',
        },
        {
          title: '所属机构',
          dataIndex: 'companyName',
          width: 100,
          align: 'center',
        },
        {
          title: '分支行',
          dataIndex: 'branchBankName',
          width: 100,
          align: 'center',
        },
      ],
    };
  }

  componentWillMount() {
    const { params } = this.props;
    this.setState({
      paymentCompanyId: params.companyId,
      paymentCompanyName: params.companyName,
      accountId: params.accountId,
    });
  }

  componentDidMount() {
    const { params } = this.props;
    if (params.id) {
      this.setState({
        isNew: false,
        moneyFrom: params.amountFrom, // 设置form表单里面的金额区间、信用分区间
        moneyTo: params.amountTo,
        creditFrom: params.creditScoreFrom,
        creditTo: params.creditScoreTo,
      });
    } else {
      this.setState({
        moneyFrom: '',
        moneyTo: '',
        creditFrom: '',
        creditTo: '',
      });
    }
    this.chooseSource();
    this.chooseBothType();
  }

  /**
   * 获取列表数据
   */
  getList = () => {
    const { pagination, searchParams, companyId } = this.state;
    this.setState({ loading: true });
    automaticPaymentRulesService
      .getAccountList(pagination.page, pagination.pageSize, companyId, searchParams)
      .then(response => {
        const { data } = response;
        this.setState({
          tableData: data,
          loading: false,
          pagination: {
            ...pagination,
            total: Number(response.headers['x-total-count'])
              ? Number(response.headers['x-total-count'])
              : 0,
            onChange: this.onChangePager,
            current: pagination.page + 1,
          },
        });
      });
  };

  /**
   * 分页点击
   */
  onChangePager = pagination => {
    const temp = {};
    temp.page = pagination.current - 1;
    temp.current = pagination.current;
    temp.pageSize = pagination.pageSize;
    this.setState(
      {
        pagination: temp,
      },
      () => {
        this.getList();
      }
    );
  };

  /**
   * 表单保存提交
   */
  handleSave = e => {
    const { form, params } = this.props;
    e.preventDefault();
    form.validateFields((err, values) => {
      let saveDate = {};
      if (err) return;
      this.setState({ loading: true });
      const {
        isNew,
        selectValue,
        moneyFrom,
        accountId,
        moneyTo,
        creditFrom,
        creditTo,
      } = this.state;
      // console.log(values);
      if (isNew) {
        saveDate = {
          enabled: values.states,
          priority: values.priority,
          description: values.ruleDescription,
          accountId: selectValue.id,
          creditScoreFrom: creditFrom,
          creditScoreTo: creditTo,
          amountFrom: moneyFrom,
          amountTo: moneyTo,
          systemSource: values.sourceSystem.key,
          propFlag: values.businessType.key,
        };
      } else {
        saveDate = {
          id: params.id,
          versionNumber: params.versionNumber,
          enabled: values.states,
          priority: values.priority,
          description: values.ruleDescription,
          accountId: selectValue.id || accountId, // 不修改情况下保存默认的accountId
          creditScoreFrom: creditFrom,
          creditScoreTo: creditTo,
          amountFrom: moneyFrom,
          amountTo: moneyTo,
          systemSource: values.sourceSystem.key,
          propFlag: values.businessType.key,
        };
      }
      let method = automaticPaymentRulesService;
      if (params.id) {
        method = automaticPaymentRulesService.updateHeader; // 是修改编辑状态
      } else {
        method = automaticPaymentRulesService.createHeader; // 是新建状态
      }
      // 表单验证
      const arr = [];
      let formStatus = true;
      // eslint-disable-next-line guard-for-in
      for (const i in saveDate) {
        arr.push(saveDate[i]);
      }
      arr.forEach(item => {
        if (item === '' || item === undefined) {
          formStatus = false;
        }
      });
      if (formStatus === false) {
        message.error('请填写完整数据');
        this.setState({
          loading: false,
        });
      } else if (moneyTo < moneyFrom || creditTo < creditFrom) {
        message.error('输入的数字区间不正确，请重新输入！！！');
        this.setState({
          loading: false,
        });
      } else {
        method(saveDate)
          .then(() => {
            message.success('保存成功！');
            this.handleCancel('save');
            this.getList();
            this.setState({ loading: false, isNew: false });
          })
          .catch(error => {
            message.error(error.response.data.message);
            this.setState({ loading: false });
          });
      }
    });
  };

  companyChange = content => {
    this.setState({
      companyId: content[0] && content[0].id,
    });
  };

  /**
   * 表单取消
   */
  handleCancel = value => {
    const { onClose } = this.props;
    onClose(value);
  };

  /**
   * 点击选择付款账号
   */
  choosePayAccount = () => {
    // console.log('===', value);
    this.setState({
      // name: value[0].userName,
    });
  };

  /**
   * 值列表-->来源系统
   */
  chooseSource = () => {
    this.getSystemValueList('ZJ_SOURCE')
      .then(res => {
        if (res.data.values.length > 0) {
          this.setState({
            systemSourceList: res.data.values,
          });
        }
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  /**
   * 值列表--> 对公对私
   */
  chooseBothType = () => {
    this.getSystemValueList('ZJ_BATCH_PAY_ACCOUNT_TYPE')
      .then(res => {
        if (res.data.values.length > 0) {
          this.setState({
            publicPrivateList: res.data.values,
          });
        }
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  // 金额从
  changeMoneyValueFrom = value => {
    this.setState({
      moneyFrom: value,
    });
  };

  // 金额至
  changeMoneyValueTo = value => {
    this.setState({
      moneyTo: value,
    });
  };

  // 信用分从
  changeCreditValueFrom = value => {
    this.setState({
      creditFrom: value,
    });
  };

  // 信用分至
  changeCreditValueTo = value => {
    this.setState({
      creditTo: value,
    });
  };

  /**
   * 对话框确认操作
   */
  handleOk = () => {
    this.setState({
      visible: false,
    });
  };

  /**
   * 对话框取消操作
   */
  onBack = () => {
    this.setState({
      visible: false,
      selectValue: {},
    });
  };

  /**
   * 弹出付款账号的对话框选择
   */
  showModal = () => {
    this.getList();
    this.setState({
      visible: true,
    });
  };

  /**
   * 模态框中的搜索
   */
  handleSearch = e => {
    e.preventDefault();
    // 获取表单数据
    const {
      form: { getFieldsValue },
    } = this.props;
    const { searchParams } = this.state;
    const params = getFieldsValue();
    this.setState(
      {
        searchParams: {
          ...searchParams,
          accountName: params.accountName ? params.accountName : '',
          accountNumber: params.accountNumber ? params.accountNumber : '',
        },
      },
      () => {
        this.getList();
      }
    );
  };

  /**
   * 模态框中的清空重置
   */
  handleClear = () => {
    const {
      form: { resetFields },
    } = this.props;
    resetFields();
  };

  render() {
    const {
      form: { getFieldDecorator },
      // user,
      params,
      company,
    } = this.props;
    const {
      loading,
      moneyFrom,
      moneyTo,
      creditFrom,
      creditTo,
      isNew,
      visible,
      columns,
      tableData,
      paymentCompanyName,
      paymentCompanyId,
      systemSourceList,
      publicPrivateList,
      selectName,
      selectValue,
      pagination,
    } = this.state;
    const formItemLayoutModal = {
      labelCol: { span: 6 },
      wrapperCol: { span: 18 },
    };
    const rowRadioSelection = {
      type: 'radio',
      columnTitle: '选择',
      onSelect: record => {
        this.setState({
          selectValue: record,
        });
      },
    };
    const formItemLayout = {
      labelCol: {
        span: 8,
      },
      wrapperCol: {
        span: 12,
      },
    };
    const formItemLayoutSwitch = {
      labelCol: {
        span: 8,
      },
      wrapperCol: {
        offset: 3,
        span: 3,
      },
    };

    return (
      <div>
        {/* 付款账号选择的模态框 */}
        <Modal
          title="付款账号选择"
          visible={visible}
          onOk={this.handleOk}
          onCancel={this.onBack}
          width="60%"
        >
          {/* 搜索区域 */}
          <div>
            <Row>
              <Form onSubmit={this.handleSearch}>
                <Row>
                  <Col span={8}>
                    <FormItem label="付款账号" {...formItemLayoutModal}>
                      {getFieldDecorator('accountNumber', {
                        rules: [{ required: false }],
                        initialValue: [selectName],
                      })(
                        <Input
                          placeholder="请输入"
                          AUTOCOMPLETE="off"
                          onPressEnter={this.handleSearch}
                        />
                      )}
                    </FormItem>
                  </Col>
                  <Col span={8}>
                    <FormItem label="付款户名" {...formItemLayoutModal}>
                      {getFieldDecorator('accountName', {
                        rules: [{ required: false }],
                        initialValue: '',
                      })(
                        <Input
                          placeholder="请输入"
                          AUTOCOMPLETE="off"
                          onPressEnter={this.handleSearch}
                        />
                      )}
                    </FormItem>
                  </Col>
                </Row>
                <div style={{ marginBottom: '10px', position: 'relative', left: '80%' }}>
                  <Button type="primary" onClick={this.handleSearch}>
                    {this.$t('搜索')}
                  </Button>&nbsp;&nbsp;&nbsp;
                  <Button onClick={this.handleClear}>重置</Button>
                </div>
              </Form>
            </Row>
          </div>
          <Table
            rowKey={record => record.id}
            columns={columns}
            dataSource={tableData}
            pagination={pagination}
            rowSelection={rowRadioSelection}
            loading={loading}
            onChange={this.onChangePager}
            bordered
            size="middle"
          />
        </Modal>
        {/* 侧边框表单 */}
        <Form>
          <FormItem label="优先级" {...formItemLayout}>
            {getFieldDecorator('priority', {
              rules: [{ required: true }],
              initialValue: isNew ? '' : params.priority,
            })(<InputNumber style={{ width: '339px', height: '32px' }} placeholder="请输入" />)}
          </FormItem>
          <FormItem label="规则描述" {...formItemLayout}>
            {getFieldDecorator('ruleDescription', {
              rules: [{ required: true }],
              initialValue: isNew ? '' : params.description,
            })(<Input placeholder="请输入" />)}
          </FormItem>
          <FormItem label="付款公司" {...formItemLayout}>
            {getFieldDecorator('paymentCompany', {
              rules: [{ required: true }],
              initialValue: [
                isNew
                  ? { id: company.id, name: company.name }
                  : { id: paymentCompanyId, name: paymentCompanyName },
              ],
            })(
              <Chooser
                type="company"
                labelKey="name"
                valueKey="id"
                // showClear={false}
                allowClear
                onChange={this.companyChange}
                single
                listExtraParams={{ setOfBooksId: company.setOfBooksId }}
              />
            )}
          </FormItem>
          <FormItem label="付款账号" {...formItemLayout}>
            {getFieldDecorator('paymentAccount', {
              rules: [{ required: true }],
              initialValue: selectValue.accountNumber || (params.accountNumber || ''),
            })(<Input placeholder="请选择" onClick={this.showModal} AUTOCOMPLETE="off" />)}
          </FormItem>
          <FormItem label="付款户名" {...formItemLayout}>
            {getFieldDecorator('paymentName', {
              rules: [{ required: false }],
              initialValue: selectValue.accountName || (params.accountName || ''),
            })(<Input disabled AUTOCOMPLETE="off" />)}
          </FormItem>
          <FormItem label="来源系统" {...formItemLayout}>
            {getFieldDecorator('sourceSystem', {
              rules: [{ required: false }],
              initialValue: isNew
                ? ''
                : { key: params.systemSource, value: params.systemSourceDesc },
            })(
              <Select placeholder="请选择" labelInValue onChange={this.chooseSource} allowClear>
                {systemSourceList.length > 0 &&
                  systemSourceList.map(item => {
                    return <Option key={item.value}>{item.name}</Option>;
                  })}
              </Select>
            )}
          </FormItem>
          <FormItem label="公私标志" {...formItemLayout}>
            {getFieldDecorator('businessType', {
              rules: [{ required: false }],
              initialValue: isNew ? '' : { key: params.propFlag, value: params.propFlagDesc },
            })(
              <Select placeholder="请选择" labelInValue onChange={this.chooseBothType} allowClear>
                {publicPrivateList.length > 0 &&
                  publicPrivateList.map(item => {
                    return <Option key={item.value}>{item.name}</Option>;
                  })}
              </Select>
            )}
          </FormItem>
          <div style={{ paddingLeft: '27%', marginBottom: '25px', color: '#666666' }}>
            <span>金额：</span>
            <InputNumber
              onChange={this.changeMoneyValueFrom}
              style={{ marginRight: '80px' }}
              size="default"
              placeholder="金额从"
              value={moneyFrom}
            />
            <InputNumber onChange={this.changeMoneyValueTo} placeholder="金额至" value={moneyTo} />
          </div>
          <div style={{ paddingLeft: '25%', marginBottom: '25px', color: '#666666' }}>
            <span>信用分：</span>
            <InputNumber
              onChange={this.changeCreditValueFrom}
              style={{ marginRight: '80px' }}
              placeholder="分数从"
              value={creditFrom}
              // defaultValue={isNew ? '' : params.creditScoreFrom}
            />
            <InputNumber
              onChange={this.changeCreditValueTo}
              placeholder="分数至"
              value={creditTo}
            />
          </div>
          <FormItem label="状态" {...formItemLayoutSwitch}>
            {getFieldDecorator('states', {
              valuePropName: 'checked',
              initialValue: typeof params.id === 'undefined' ? false : params.enabled,
            })(
              <Switch
                checkedChildren={<Icon type="check" />}
                unCheckedChildren={<Icon type="cross" />}
              />
            )}
          </FormItem>
          <div className="slide-footer">
            <Button
              type="primary"
              htmlType="submit"
              loading={loading}
              style={{ margin: '0 20px' }}
              onClick={this.handleSave}
            >
              保存
            </Button>
            <Button onClick={this.handleCancel}>取消</Button>
          </div>
        </Form>
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

export default connect(mapStateToProps)(Form.create()(AutoPayRuleAdd));

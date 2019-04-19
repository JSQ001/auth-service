import React from 'react';
import { connect } from 'dva';
import 'styles/fund/account.scss';
import { Form, Input, Button, Switch, message, Table, Modal, Icon } from 'antd';

import accountService from './account-authority.service';

const FormItem = Form.Item;

class accountDistributionAdd extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false, // 加载中
      bankList: [], // 银行账户列表
      visible: false, // 控制行信息选择银行账户的弹窗的显示
      adbaseId: '', // 从头传过来的baseID
      selectDate: {}, // 银行账户信息选中的值
      pagination: {
        total: 0,
        page: 0,
        pageSize: 10,
        current: 1,
        showSizeChanger: true,
        showQuickJumper: true,
        showTotal: (total, range) =>
          this.$t('common.show.total', {
            range0: `${range[0]}`,
            range1: `${range[1]}`,
            total,
          }),
      },
      // 列表头
      columns: [
        {
          title: this.$t('fund.bank.account') /* 银行账号 */,
          dataIndex: 'accountNumber',
          width: 170,
        },
        {
          title: this.$t('fund.account.name') /* 账户名称 */,
          dataIndex: 'accountName',
          width: 170,
        },
        {
          title: this.$t('fund.currency.code') /* 币种 */,
          dataIndex: 'currencyCode',
          width: 100,
        },
      ],
    };
  }

  componentWillMount() {
    const { params } = this.props;
    const { baseId } = this.props;
    this.setState({
      adbaseId: baseId,
      updateDateU: params,
    });
  }

  componentDidMount() {
    this.getBackList();
  }

  /**
   * 获取开户银行列表
   */
  getBackList = accountNumber => {
    const { pagination } = this.state;
    accountService
      .getAccountOpenMaintenanceList(pagination.page, pagination.pageSize, accountNumber)
      .then(response => {
        this.setState({
          bankList: response.data,
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
   * 选择银行账户
   */
  showDrawer = () => {
    this.setState({
      visible: true,
    });
    this.getBackList();
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
        this.getBackList();
      }
    );
  };

  /**
   * 新建的取消
   * */
  handleCancel = value => {
    const { onClose } = this.props;
    onClose(value);
  };

  /**
   * 银行账户的搜索
   */
  search = e => {
    const { form } = this.props;
    e.preventDefault();
    form.validateFields((err, values) => {
      if (values.account) {
        this.getBackList(values.account);
      } else {
        message.error(this.$t('fund.fill.complete.data')); // 请填写完整数据
        this.getBackList();
      }
    });
  };

  /**
   * 搜索条件重置
   */
  searchClear = e => {
    const { form } = this.props;
    e.preventDefault();
    form.resetFields();
    this.getBackList();
  };

  /**
   * 选择银行账户的取消
   */
  onBack = () => {
    this.setState({
      visible: false,
      selectDate: {},
    });
  };

  /**
   * 保存关闭
   */
  onClose = () => {
    this.setState({
      visible: false,
    });
  };

  /**
   * 表单提交
   */
  handleSave = e => {
    const { adbaseId, selectDate, updateDateU } = this.state;
    const { form } = this.props;
    let saveDate = {};
    e.preventDefault();
    form.validateFields((err, values) => {
      if (updateDateU.versionNumber) {
        saveDate = {
          id: updateDateU.id,
          versionNumber: updateDateU.versionNumber,
          baseId: adbaseId,
          accountId: selectDate.id || updateDateU.accountId,
          queryFlag: values.queryFlag,
          gatherFlag: values.gatherFlag,
          payFlag: values.payFlag,
          checkFlag: values.checkFlag,
        };
      } else {
        saveDate = {
          baseId: adbaseId,
          accountId: selectDate.id || '',
          queryFlag: values.queryFlag,
          gatherFlag: values.gatherFlag,
          payFlag: values.payFlag,
          checkFlag: values.checkFlag,
        };
      }
      if (!values.accountNumber) {
        message.error(this.$t('fund.fill.complete.data')); // 请填写完整数据
      } else {
        accountService
          .distributionSave(saveDate)
          .then(() => {
            message.success(this.$t('fund.save.successful1')); // 保存成功！
            this.handleCancel('save');
          })
          .catch(erro => {
            message.error(erro.response.data.message);
          });
      }
    });
  };

  render() {
    const {
      form: { getFieldDecorator },
    } = this.props;
    const {
      loading,
      columns,
      visible,
      bankList,
      selectDate,
      pagination,
      updateDate,
      updateDateU,
    } = this.state;
    const formItemLayout = {
      labelCol: {
        span: 8,
      },
      wrapperCol: {
        span: 12,
      },
    };
    const formItemLayout1 = {
      labelCol: {
        span: 13,
      },
      wrapperCol: {
        span: 2,
      },
    };
    const formItemLayout2 = {
      labelCol: {
        span: 3,
      },
      wrapperCol: {
        span: 5,
      },
    };
    const rowRadioSelection = {
      type: 'radio',
      columnTitle: this.$t('fund.choose') /* 选择 */,
      onSelect: selectedRowKeys => {
        this.setState({ selectDate: selectedRowKeys });
        this.setState({ updateDate: selectedRowKeys });
      },
    };
    return (
      <div>
        <Modal
          title={this.$t('fund.bank.account.to.choose')}
          visible={visible}
          onOk={this.onClose}
          onCancel={this.onBack}
          width={800}
        >
          {/* 银行账号选择 */}
          {/* 搜索区域 */}
          <div>
            <Form style={{ padding: '10px' }}>
              <FormItem label={this.$t('fund.bank.account')} {...formItemLayout2}>
                {/* 银行账号 */}
                {getFieldDecorator('account', {
                  initialValue: '',
                })(<Input AUTOCOMPLETE="off" onPressEnter={this.search} />)}
              </FormItem>
              <div style={{ position: 'relative', left: '80%' }}>
                <Button type="primary" onClick={this.search}>
                  {this.$t('fund.search')}
                </Button>&nbsp;&nbsp;&nbsp;
                <Button onClick={this.searchClear}>{this.$t('fund.reset')}</Button>
                {/* 重置 */}
              </div>
            </Form>
          </div>
          <Table
            rowKey={record => record.id}
            columns={columns}
            dataSource={bankList}
            pagination={pagination}
            rowSelection={rowRadioSelection}
            loading={loading}
            onChange={this.onChangePager}
            bordered
            size="middle"
          />
        </Modal>
        <Form>
          <FormItem label={this.$t('fund.bank.account')} {...formItemLayout}>
            {/* 银行账号 */}
            {getFieldDecorator('accountNumber', {
              initialValue: selectDate.accountNumber
                ? updateDate.accountNumber
                : updateDateU
                  ? updateDateU.accountNumber
                  : '',
            })(<Input onClick={this.showDrawer} AUTOCOMPLETE="off" />)}
          </FormItem>
          <FormItem label={this.$t('fund.account.name')} {...formItemLayout}>
            {/* 账户名称 */}
            {getFieldDecorator('accountName', {
              rules: [{ required: false }],
              initialValue: selectDate.id
                ? selectDate.accountName
                : updateDateU
                  ? updateDateU.accountName
                  : '',
            })(<Input disabled onChange />)}
          </FormItem>
          <FormItem label={this.$t('fund.currency.code')} {...formItemLayout}>
            {/* 币种 */}
            {getFieldDecorator('currencyCode', {
              rules: [{ required: false, message: this.$t('common.please.select') }],
              initialValue: selectDate.id
                ? selectDate.currencyCode
                : updateDateU
                  ? updateDateU.currencyCode
                  : '',
            })(<Input disabled onChange />)}
          </FormItem>
          <FormItem label={this.$t('fund.query.permissions')} {...formItemLayout1}>
            {/* 查询权限 */}
            {getFieldDecorator('queryFlag', {
              valuePropName: 'checked',
              initialValue: typeof updateDateU.id === 'undefined' ? true : updateDateU.queryFlag,
            })(
              <Switch
                checkedChildren={<Icon type="check" />}
                unCheckedChildren={<Icon type="cross" />}
              />
            )}
          </FormItem>
          <FormItem label={this.$t('fund.payment.permissions')} {...formItemLayout1}>
            {/* 付款权限 */}
            {getFieldDecorator('payFlag', {
              valuePropName: 'checked',
              initialValue: typeof updateDateU.id === 'undefined' ? true : updateDateU.payFlag,
            })(
              <Switch
                checkedChildren={<Icon type="check" />}
                unCheckedChildren={<Icon type="cross" />}
              />
            )}
          </FormItem>
          <FormItem label={this.$t('fund.receiving.permissions')} {...formItemLayout1}>
            {/* 收款权限 */}
            {getFieldDecorator('gatherFlag', {
              valuePropName: 'checked',
              initialValue: typeof updateDateU.id === 'undefined' ? true : updateDateU.gatherFlag,
            })(
              <Switch
                checkedChildren={<Icon type="check" />}
                unCheckedChildren={<Icon type="cross" />}
              />
            )}
          </FormItem>
          <FormItem label={this.$t('fund.check.permissions')} {...formItemLayout1}>
            {/* 对账权限 */}
            {getFieldDecorator('checkFlag', {
              valuePropName: 'checked',
              initialValue: typeof updateDateU.id === 'undefined' ? true : updateDateU.checkFlag,
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
              {this.$t('fund.save')}
              {/* 保存 */}
            </Button>
            <Button onClick={this.handleCancel}>{this.$t('fund.cancel')}</Button>
            {/* 取消 */}
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
export default connect(mapStateToProps)(Form.create()(accountDistributionAdd));

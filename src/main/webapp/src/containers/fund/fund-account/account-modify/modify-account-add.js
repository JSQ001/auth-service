import React from 'react';
// import Chooser from 'widget/chooser';
import { Form, Modal, Input, Table, Card, message, Button } from 'antd';
import { connect } from 'dva';
import accountService from './modify-account.service';
import ModifyAccountAddInformation from './modify-account-add-information';

const FormItem = Form.Item;
class ModifyAccountAdd extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      bankList: [], // 银行账户列表
      visible: false, // 控制行信息选择银行账户的弹窗的显示
      selectDate: {}, // 银行账户信息选中的值
      isNew: true, // 是否为新建单据
      isCan1: false, // 是否为新建单据
      isDisable: true, // 可用标志
      applicationInformation: {},
      backInformation: [],
      pagination: {
        total: 0,
        page: 0,
        pageSize: 10,
        current: 1,
      },
      columns: [
        {
          title: '银行账号',
          dataIndex: 'accountNumber',
          key: 'accountnumber',
          align: 'center',
          width: 150,
          tooltips: true,
        },
        {
          title: '账户户名',
          dataIndex: 'accountName',
          align: 'center',
          width: 150,
          tooltips: true,
        },
      ],
    };
  }

  componentDidMount() {
    const { match } = this.props;
    if (match.params.id === 'new') {
      console.log('new');
    } else {
      this.setState({
        isNew: false,
      });
      this.getModifyAccountDetail(match.params.id);
    }
  }

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
   * 点击确定
   */
  onOk = () => {
    const { selectDate, isCan1 } = this.state;
    this.setState({
      applicationInformation: selectDate,
      visible: false,
    });
    if (selectDate.accountDepositType === 'ZJ_TIME') {
      console.log(isCan1);
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
   * 取消
   */
  onBack = () => {
    const {
      form: { resetFields },
    } = this.props;
    resetFields('modalAccount');
    this.setState({
      visible: false,
      selectDate: {},
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
   * 获取开户银行列表
   */
  getBackList = () => {
    const { pagination } = this.state;
    const {
      form: { getFieldValue },
    } = this.props;
    const accountNumber = getFieldValue('modalAccount');
    accountService
      .getPageByCondition(pagination.page, pagination.pageSize, accountNumber)
      .then(response => {
        this.setState({
          bankList: response.data,
          loading: false,
          pagination: {
            ...pagination,
            total: Number(response.headers['x-total-count'])
              ? Number(response.headers['x-total-count'])
              : 0,
            pageSize: pagination.pageSize,
            onChange: this.onChangePager,
            current: pagination.page + 1,
          },
        });
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
    const { columns, pagination, visible, bankList, loading, selectDate } = this.state;
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
    const rowRadioSelection = {
      type: 'radio',
      columnTitle: '选择',
      onSelect: selectedRowKeys => {
        this.setState({
          selectDate: selectedRowKeys,
        });
      },
    };
    const accountFormItemLayout = {
      labelCol: {
        span: 3,
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
              <FormItem label="银行账号" {...formItemLayout}>
                {getFieldDecorator('accountNumber', {
                  rules: [{ required: true }],
                  initialValue: selectDate.accountNumber ? selectDate.accountNumber : '',
                })(<Input onClick={this.showDrawer} autoComplete="off" />)}
              </FormItem>
              <Modal title="银行账号选择" visible={visible} onOk={this.onOk} onCancel={this.onBack}>
                {/* 共total条数据 */}
                <div className="table-header-title">
                  {this.$t('common.total', { total: pagination.total ? pagination.total : '0' })}
                </div>
                {/* 搜索区域 */}
                <Form style={{ paddingBottom: '20px' }}>
                  <Form.Item label="银行账号" {...accountFormItemLayout}>
                    {getFieldDecorator('modalAccount', {
                      initialValue: '',
                    })(<Input autoComplete="off" onPressEnter={this.modalSearch} />)}
                  </Form.Item>
                  <div style={{ position: 'relative', left: '65%' }}>
                    <Button type="primary" onClick={this.modalSearch}>
                      {this.$t('搜索')}
                    </Button>&nbsp;&nbsp;&nbsp;
                    <Button onClick={this.modalSearchClear}>重置</Button>
                  </div>
                </Form>
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
              <FormItem label="单据编号" {...formItemLayout}>
                {getFieldDecorator('documentNumber', {
                  rules: [{ required: true }],
                  initialValue: backInformation,
                })(<Input setfieldsvalue={applicationInformation.documentNumber} disabled />)}
              </FormItem>
              <FormItem label="单据类型" {...formItemLayout}>
                {<Input defaultValue="账户变更" disabled />}
              </FormItem>
              <FormItem label="申请人" {...formItemLayout}>
                {getFieldDecorator('userName', {
                  rules: [{ required: true }],
                  initialValue: user.userName ? user.userName : '',
                })(<Input setfieldsvalue={user.userName} disabled />)}
              </FormItem>
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

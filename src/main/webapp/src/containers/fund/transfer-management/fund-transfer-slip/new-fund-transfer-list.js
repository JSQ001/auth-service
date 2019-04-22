import React from 'react';
import { Form, Row, Col, Input, Select, DatePicker, Button, message } from 'antd';
import { connect } from 'dva';
import Lov from 'widget/Template/lov';
import moment from 'moment';
import FundEditablTable from '../../fund-components/fund-editable-table';
import 'styles/fund/account.scss';
import newFundTransferListService from './new-fund-transfer-list.service';

const { Option } = Select;
const FormItem = Form.Item;

class NewFundTransferList extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      tableData: [],
      paymentMethods: [],
      loading: false,
      documentNumber: '',
      headId: '',
      accountInfo: '',
      selectedRowKeys: [],
      adjustBaseInfo: '', // 调拨单头
      columns: [
        {
          title: '序号',
          dataIndex: 'sequenceNumber',
          type: 'sort',
          editable: false,
          width: 30,
          render: (text, record, index) => {
            const otherIndex = index + 1;
            return otherIndex;
          },
        },
        {
          title: '调入公司',
          dataIndex: 'adjustInCorpDesc',
          saveDataIndex: 'adjustInCorp',
          type: 'fundCompanyLov',
          editable: true,
          width: 200,
        },
        {
          title: '调入账号',
          dataIndex: 'adjustInAccount',
          saveDataIndex: 'adjustInAccount',
          type: 'fundAccountLov',
          fundLovCode: 'account',
          editable: true,
          width: 200,
          linkage: ['adjustInOpenBank', 'currency'],
        },
        {
          title: '调入银行',
          dataIndex: 'adjustInOpenBank',
          editable: false,
          width: 200,
        },
        {
          title: '金额',
          dataIndex: 'amount',
          saveDataIndex: 'amount',
          editable: true,
          width: 150,
        },
        {
          title: '币种',
          dataIndex: 'currency',
          saveDataIndex: 'currencyCode',
          editable: false,
          width: 100,
        },
        {
          title: '摘要',
          dataIndex: 'description',
          saveDataIndex: 'description',
          editable: true,
          width: 200,
        },
        {
          title: '调拨申请单号',
          dataIndex: 'requisitionNumber',
          saveDataIndex: 'requisitionNumber',
          type: 'input',
          required: false,
          editable: true,
          inputDisableEdit: true,
          width: 100,
        },
        {
          title: '付款用途',
          dataIndex: 'paymentUseCodeDesc',
          saveDataIndex: 'paymentUseCode',
          initialValue: '费控现金流量表',
          editable: true,
          width: 200,
        },
        {
          title: '支付状态',
          dataIndex: 'paymentStatusDesc',
          saveDataIndex: 'paymentStatus',
          initialValue: '未支付',
          editable: true,
          width: 100,
        },
      ],
    };
  }

  componentDidMount() {
    this.getPaymentMethods();
    const { match } = this.props;
    if (match.params.id) {
      this.getBaseInfoHead(match.params.id);
    }
  }

  getBaseInfoHead = id => {
    this.setState({
      loading: true,
    });
    newFundTransferListService
      .getBaseInfoHead(id)
      .then(response => {
        this.setState({
          adjustBaseInfo: response.data.adjustBaseInfo,
          tableData: response.data.adjustLineInfos,
          loading: false,
        });
      })
      .catch(error => {
        message.error(error.response.data.message);
      });
  };

  /**
   * onRef调用子组件方法
   */
  onRef = ref => {
    this.child = ref;
  };

  /**
   * 添加行
   */
  addLine = () => {
    this.child.handleAdd();
  };

  /**
   * 删除行
   */
  deleteLine = () => {
    const { selectedRowKeys, headId } = this.state;
    const params = {
      applyIds: selectedRowKeys,
      headerId: headId,
    };
    if (params.headerId) {
      newFundTransferListService.batchDelete(params).then(response => {
        if (response.data) {
          message.success('删除成功！');
          this.child.handleDelete(selectedRowKeys);
        }
      });
    } else {
      this.child.handleDelete(selectedRowKeys);
    }
  };

  /**
   * 保存
   */
  saveTable = value => {
    const { form, user } = this.props;
    const { accountInfo, documentNumber } = this.state;
    this.setState({
      loading: true,
    });
    const adjustLineInfos = value.map(item => {
      return {
        adjustInCorp: item.adjustInCorp.key, // 调入公司ID
        adjustInAccountId: item.adjustInAccount.key, // 调入账号id
        amount: item.amount, // 金额
        description: item.description, // 描述
        makeCapital: user.id, // 制单人，前端传当前登录人ID
        paymentStatus: 'UNPAID', // 支付状态，默认UNPAID，未支付
        paymentUseCode: 'ZJ10101',
      };
    });
    form.validateFields((err, saveValue) => {
      let createOrUpdateValue = {};
      if (!err) {
        const adjustBaseInfo = {
          adjustBillType: 'ZJ_ADJUST', // 单据类型
          handleStatus: 'PAYABLE', // 处理状态
          headStatus: 'TO_SEND', // 头状态
          billStatus: 'ZJ_ADD', // 审批状态
          paymentMethod: saveValue.paymentMethod.key, // 付款方式
          belongCorpId: saveValue.company.id, // 所属公司
          belongDeptId: saveValue.department.id.split('|')[0], // 所属部门
          belongDeptOid: saveValue.department.id.split('|')[1],
          adjustOutAccountId: saveValue.accountNumber.id, // 调出账号ID
          billDate: moment(saveValue.date).format(), // 单据日期
          description: saveValue.description, // 描述
          currencyCode: accountInfo.currencyCode, // 币种
          employeeId: user.id, // 制单人
        };
        createOrUpdateValue = {
          adjustBaseInfo,
          adjustLineInfos,
        };
        newFundTransferListService
          .baseInfoCreateOrUpdate(createOrUpdateValue)
          .then(response => {
            if (response.data) {
              message.success('保存成功');
              if (!documentNumber) {
                this.setState({
                  documentNumber: response.data.adjustBatchNumber,
                });
              }
              this.setState({
                loading: false,
              });
            }
          })
          .catch(error => {
            message.error(error.response.data.message);
          });
      }
    });
  };

  /**
   * 保存表格
   */
  save = () => {
    this.child.saveTable();
  };

  /**
   * 获取付款方式值列表
   * @param code :值列表代码
   * @param name :值列表名称
   */
  getPaymentMethods = () => {
    this.getSystemValueList('ZJ_PAYMENT_TYPE').then(response => {
      const paymentMethods = response.data.values.map(item => {
        const option = {
          key: item.value,
          id: item.value,
          value: item.name,
        };
        return option;
      });
      this.setState({
        paymentMethods,
      });
    });
  };

  /**
   * 选择公司
   */
  onChangeLovCompany = value => {
    const accountLovPrams = {
      companyId: value.id,
    };
    this.setState({ accountLovPrams });
  };

  /**
   * 选择户名
   */
  onChangeAccount = value => {
    this.setState({
      accountInfo: value,
    });
  };

  /**
   * 行onSelectChange
   */
  onSelectChange = selectedRowKeys => {
    this.setState({ selectedRowKeys });
  };

  render() {
    const {
      form: { getFieldDecorator },
      company,
      user,
    } = this.props;
    const {
      columns,
      tableData,
      loading,
      paymentMethods,
      accountLovPrams,
      accountInfo,
      selectedRowKeys,
      adjustBaseInfo,
      documentNumber,
    } = this.state;
    const rowSelection = {
      type: 'radio',
      selectedRowKeys,
      onChange: this.onSelectChange,
    };
    return (
      <div className="train">
        <div className="table-header" style={{ backgroundColor: '#FAFAFA' }}>
          <Form>
            <Row>
              <Col span={5}>
                <FormItem label="单据编号" style={{ marginBottom: '0px' }}>
                  {getFieldDecorator('documentNumber', {
                    initialValue: adjustBaseInfo
                      ? adjustBaseInfo.adjustBatchNumber
                      : documentNumber,
                  })(<Input disabled />)}
                </FormItem>
              </Col>
              <Col span={5} offset={1}>
                <FormItem label="所属公司" style={{ marginBottom: '0px' }}>
                  {getFieldDecorator('company', {
                    initialValue: adjustBaseInfo
                      ? { id: adjustBaseInfo.belongCorpId, name: adjustBaseInfo.belongCorpName }
                      : { id: user.companyId, name: user.companyName },
                  })(
                    <Lov
                      code="company"
                      valueKey="id"
                      labelKey="name"
                      extraParams={{ setOfBooksId: company.setOfBooksId }}
                      onChange={this.onChangeLovCompany}
                      single
                    />
                  )}
                </FormItem>
              </Col>
              <Col span={5} offset={1}>
                <FormItem label="所属部门" style={{ marginBottom: '0px' }}>
                  {getFieldDecorator('department', {
                    initialValue: adjustBaseInfo
                      ? {
                          id: `${adjustBaseInfo.belongDeptId}|${adjustBaseInfo.belongDeptOid}`,
                          name: adjustBaseInfo.belongDeptName,
                        }
                      : {
                          id: `${user.departmentId}|${user.departmentOid}`,
                          name: user.departmentName,
                        },
                  })(
                    <Lov
                      code="department"
                      valueKey="departmentId"
                      labelKey="name"
                      extraParams={{ setOfBooksId: user.tenantId }}
                      single
                    />
                  )}
                </FormItem>
              </Col>
              <Col span={5} offset={1}>
                <FormItem label="付款方式" style={{ marginBottom: '0px' }}>
                  {getFieldDecorator('paymentMethod', {
                    initialValue: adjustBaseInfo
                      ? {
                          key: adjustBaseInfo.paymentMethod,
                          label: adjustBaseInfo.paymentMethodName,
                        }
                      : '',
                  })(
                    <Select placeholder="请选择" labelInValue allowClear>
                      {paymentMethods.map(item => {
                        return <Option key={item.id}>{item.value}</Option>;
                      })}
                    </Select>
                  )}
                </FormItem>
              </Col>
              <Col span={5}>
                <FormItem label="调出账号" style={{ marginBottom: '0px' }}>
                  {getFieldDecorator('accountNumber', {
                    initialValue: adjustBaseInfo
                      ? {
                          id: adjustBaseInfo.adjustOutAccountId,
                          accountNumber: adjustBaseInfo.adjustOutAccountNumber,
                        }
                      : '',
                  })(
                    <Lov
                      code="bankaccount_choose"
                      valueKey="id"
                      labelKey="accountNumber"
                      single
                      onChange={(value, data) => {
                        this.onChangeAccount(value, data);
                      }}
                      extraParams={
                        accountLovPrams
                          ? { companyId: accountLovPrams.companyId }
                          : { companyId: adjustBaseInfo.belongCorpId }
                      }
                    />
                  )}
                </FormItem>
              </Col>
              <Col span={5} offset={1}>
                <FormItem label="调出账户" style={{ marginBottom: '0px' }}>
                  {getFieldDecorator('account', {
                    initialValue: accountInfo
                      ? accountInfo.accountName
                      : adjustBaseInfo.adjustOutAccountName,
                  })(<Input disabled />)}
                </FormItem>
              </Col>
              <Col span={5} offset={1}>
                <FormItem label="单据日期" style={{ marginBottom: '0px' }}>
                  {getFieldDecorator('date', {
                    initialValue: adjustBaseInfo ? moment(adjustBaseInfo.billDate) : moment(),
                  })(<DatePicker />)}
                </FormItem>
              </Col>
              <Col span={5} offset={1}>
                <FormItem label="制单人" style={{ marginBottom: '0px' }}>
                  {getFieldDecorator('account', {
                    initialValue: adjustBaseInfo ? adjustBaseInfo.employeeName : user.userName,
                  })(<Input disabled />)}
                </FormItem>
              </Col>
              <Col span={11}>
                <FormItem label="备注">
                  {getFieldDecorator('description', {
                    initialValue: adjustBaseInfo ? adjustBaseInfo.description : '',
                  })(<Input placeholder="请输入" />)}
                </FormItem>
              </Col>
            </Row>
          </Form>
        </div>
        <div className="table-header-buttons" style={{ marginBottom: '20px', marginTop: '20px' }}>
          <Row>
            <Col span={12}>
              <Button
                style={{ marginRight: '10px' }}
                type="primary"
                onClick={e => {
                  e.preventDefault();
                  e.stopPropagation();
                  this.addLine();
                }}
              >
                新增行
              </Button>
              <Button
                style={{ marginRight: '10px' }}
                disabled={selectedRowKeys.length === 0}
                type="danger"
                onClick={e => {
                  e.preventDefault();
                  e.stopPropagation();
                  this.deleteLine();
                }}
              >
                删除行
              </Button>
              <Button
                style={{ marginRight: '10px' }}
                type="primary"
                onClick={e => {
                  e.preventDefault();
                  e.stopPropagation();
                  this.createFromApply();
                }}
              >
                从申请单创建
              </Button>
              <Button
                style={{ marginRight: '10px' }}
                type="primary"
                onClick={e => {
                  e.preventDefault();
                  e.stopPropagation();
                  this.save();
                }}
              >
                保存
              </Button>
              <Button
                type="primary"
                onClick={e => {
                  e.preventDefault();
                  e.stopPropagation();
                  this.submit();
                }}
              >
                提交
              </Button>
            </Col>
          </Row>
        </div>
        <FundEditablTable
          loading={loading}
          onRef={this.onRef}
          saveTable={this.saveTable}
          rowSelection={rowSelection}
          columns={columns}
          dataSource={tableData}
        />
      </div>
    );
  }
}

/**
 * 建立组件和数据的映射关系 注意state必传 返回的是需要绑定的model
 * @param {*} state
 */
function map(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
  };
}
/**
 * 关联 model
 */
export default connect(map)(Form.create()(NewFundTransferList));

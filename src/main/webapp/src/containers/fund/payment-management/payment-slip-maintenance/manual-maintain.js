import React from 'react';
import {
  Form,
  Table,
  Button,
  message,
  Input,
  Alert,
  Modal,
  Row,
  Col,
  Select,
  DatePicker,
  InputNumber,
  Icon,
} from 'antd';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import moment from 'moment';
import Chooser from 'widget/chooser';
import PaymentMaintenanceService from './payment-maintenance-service';

const { confirm } = Modal;
const { Option } = Select;
const FormItem = Form.Item;
const selectWidth = { width: '100%' };

class ManualMaintain extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      dataTemp: [], // 新增时用来push到tableData中
      tableData: [], // 数据列表
      visible: false, // 选择银行账号
      bankOptions: [], // 开户银行列表
      paymentMethodOptions: [], // 付款方式值列表
      paymentPurpose: [], // 付款用途值列表
      publicPrivateSigns: [], // 公私标志
      discountSign: [], // 卡折标志
      ifPayment: [], // 是否支付
      // paymentBaseInfo: {}, // 保存的头数据
      isTrue: false,
      pagination: {
        total: 0, // 数据总数
        page: 0, // 用于计算页数
        pageSize: 10, // 每页条数
        current: 1, // 当前页数
        showSizeChanger: true, // 是否可以改变 pageSize
        showQuickJumper: true, // 是否可以快速跳转至某页
        showTotal: (total, range) => `显示${range[0]}-${range[1]} 共 ${total} 条`, // 用于显示数据总量和当前数据顺序
      },
      pagination1: {
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
          title: '单据流水号',
          dataIndex: 'tradeCode',
          width: 100,
          align: 'center',
        },
        {
          title: '收款户名',
          dataIndex: 'gatherAccountName',
          width: 100,
          align: 'center',
          render: (value, record, index) => {
            const { status } = this.state;
            return status !== 'BATCH_PAYMENT' ? (
              <Input
                placeholder="请输入"
                defaultValue={record.gatherAccountName ? record.gatherAccountName : value}
                onChange={e => this.onDescChange(e, index, 'gatherAccountName')}
                style={{ textAlign: 'center' }}
              />
            ) : (
              <span>{record.gatherAccountName}</span>
            );
          },
        },
        {
          title: '收款分行',
          dataIndex: 'gatherBranchBankName',
          width: 100,
          align: 'center',
          render: (value, record, index) => {
            const { status } = this.state;
            return status !== 'BATCH_PAYMENT' ? (
              <Input
                placeholder="请输入"
                defaultValue={record.gatherBranchBankName ? record.gatherBranchBankName : value}
                onChange={e => this.onDescChange(e, index, 'gatherBranchBankName')}
                style={{ textAlign: 'center' }}
              />
            ) : (
              <span>{record.gatherBranchBankName}</span>
            );
          },
        },
        {
          title: '收款账号',
          dataIndex: 'gatherAccount',
          width: 100,
          align: 'center',
          render: (gatherAccount, record, index) => {
            const { status } = this.state;
            return status !== 'BATCH_PAYMENT' ? (
              <Input
                placeholder="请输入"
                defaultValue={record.gatherAccount ? record.gatherAccount : gatherAccount}
                onChange={e => this.onDescChange(e, index, 'gatherAccount')}
                style={{ textAlign: 'center' }}
              />
            ) : (
              <span>{record.gatherAccount}</span>
            );
          },
        },
        {
          title: '金额',
          dataIndex: 'amount',
          width: 100,
          align: 'center',
          render: (value, record, index) => {
            const { status } = this.state;
            return status !== 'BATCH_PAYMENT' ? (
              <InputNumber
                placeholder="请输入"
                defaultValue={record.amount ? record.amount : value}
                /* eslint-disable */
                onChange={value => this.handleValueChange(value, index, 'amount')}
                /* eslint-enable */
                style={{ textAlign: 'center' }}
              />
            ) : (
              <span>{record.amount}</span>
            );
          },
        },
        {
          title: '付款用途',
          dataIndex: 'paymentPurposeDesc',
          width: 100,
          align: 'center',
          render: (value, record, index) => {
            const { paymentPurpose } = this.state;
            const { status } = this.state;
            return status !== 'BATCH_PAYMENT' ? (
              <Select
                placeholder="请选择"
                style={selectWidth}
                defaultValue={{ key: record.paymentPurposeDesc, label: record.paymentPurpose }}
                labelInValue
                /* eslint-disable */
                onChange={value => this.handleValueChange(value, index, 'paymentPurposeDesc')}
                /* eslint-enable */
              >
                {paymentPurpose.map(dimeItem => {
                  return <Option key={dimeItem.value}>{dimeItem.name}</Option>;
                })}
              </Select>
            ) : (
              <span>{record.paymentPurpose}</span>
            );
          },
        },
        {
          title: '摘要',
          dataIndex: 'description',
          width: 100,
          align: 'center',
          render: (value, record, index) => {
            const { status } = this.state;
            return status !== 'BATCH_PAYMENT' ? (
              <Input
                placeholder="请输入"
                defaultValue={record.description ? record.description : value}
                min={10}
                onChange={e => this.onDescChange(e, index, 'description')}
                style={{ textAlign: 'center' }}
              />
            ) : (
              <span>{record.description}</span>
            );
          },
        },
        {
          title: '公私标志',
          dataIndex: 'propFlagDesc',
          width: 100,
          align: 'center',
          render: (value, record, index) => {
            const { publicPrivateSigns } = this.state;
            const { status } = this.state;
            return status !== 'BATCH_PAYMENT' ? (
              <Select
                placeholder="请选择"
                style={selectWidth}
                defaultValue={{ key: record.propFlag, label: record.propFlagDesc }}
                labelInValue
                /* eslint-disable */
                onChange={value => this.handleValueChange(value, index, 'propFlagDesc')}
                /* eslint-enable */
              >
                {publicPrivateSigns.map(dimeItem => {
                  return <Option key={dimeItem.value}>{dimeItem.name}</Option>;
                })}
              </Select>
            ) : (
              <span>{record.propFlagDesc}</span>
            );
          },
        },
        {
          title: '卡折标志',
          dataIndex: 'cardSignDesc',
          width: 100,
          align: 'center',
          render: (value, record, index) => {
            const { discountSign } = this.state;
            const { status } = this.state;
            return status !== 'BATCH_PAYMENT' ? (
              <Select
                placeholder="请选择"
                style={selectWidth}
                defaultValue={
                  { key: record.cardSign, label: record.cardSignDesc } || {
                    key: 'BANK_CARD',
                    label: '银行卡',
                  }
                }
                labelInValue
                /* eslint-disable */
                onChange={value => this.handleValueChange(value, index, 'cardSignDesc')}
                /* eslint-enable */
              >
                {discountSign.map(dimeItem => {
                  return <Option key={dimeItem.value}>{dimeItem.name}</Option>;
                })}
              </Select>
            ) : (
              <span>{record.cardSignDesc}</span>
            );
          },
        },
        {
          title: '是否付款',
          dataIndex: 'ifPaymentDesc',
          width: 100,
          align: 'center',
          render: (value, record, index) => {
            const { ifPayment } = this.state;
            const { status } = this.state;
            return status !== 'BATCH_PAYMENT' ? (
              <Select
                placeholder="请选择"
                style={selectWidth}
                defaultValue={{ key: record.ifPayment, label: record.ifPaymentDesc }}
                labelInValue
                /* eslint-disable */
                onChange={value => this.handleValueChange(value, index, 'ifPaymentDesc')}
                /* eslint-enable */
              >
                {ifPayment.map(dimeItem => {
                  return <Option key={dimeItem.value}>{dimeItem.name}</Option>;
                })}
              </Select>
            ) : (
              <span>{record.ifPaymentDesc}</span>
            );
          },
        },
      ],
      columns1: [
        {
          title: '银行账号',
          dataIndex: 'accountNumber',
          width: '15%',
          align: 'center',
        },
        {
          title: '账户名称',
          dataIndex: 'accountName',
          width: '15%',
          align: 'center',
        },
        {
          title: '币种',
          dataIndex: 'currencyCode',
          width: '15%',
          align: 'center',
        },
      ],
    };
  }

  componentDidMount() {
    const { match } = this.props;
    if (match.params.id) {
      this.setState({
        // isNew: false,
        status: match.params.status, // 付款单类型
        userId: match.params.id,
      });
      this.getList(match.params.id);
      this.getUserList(match.params.paymentBatchNumber);
    }
    this.getAccountBank(); // 开户银行列表
    this.getPaymentMethodOptions(); // 付款方式值列表
    this.getPaymentPurpose(); // 付款用途值列表
    this.getPublicPrivateSigns(); // 公私标志
    this.getDiscountSign(); // 卡折标志
    this.getIfPayment(); // 是否付款
  }

  /**
   * 数据列表
   */
  getList = async id => {
    const { pagination } = this.state;
    this.setState({ loading: true });
    await PaymentMaintenanceService.getManualList(pagination.page, pagination.pageSize, id).then(
      response => {
        const { data } = response;
        let amountNum = 0; // 总金额
        if (data.length > 0) {
          data.forEach(val => {
            amountNum += val.amount;
          });
        }
        this.setState({
          allAccount: amountNum,
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
      }
    );
  };

  /**
   * 根据头单据编号获取数据
   * @param {*} number
   */
  getUserList(number) {
    const { pagination } = this.state;
    const batchNumber = { paymentBatchNumber: number };
    PaymentMaintenanceService.getPaymentQueryList(
      pagination.page,
      pagination.pageSize,
      batchNumber
    ).then(response => {
      const { data } = response;
      this.setState({
        billType: data[0].billType || '',
        paymentBatchNumber: data[0].paymentBatchNumber || '', // 付款单号
        paymentMethodDesc: data[0].paymentMethodDesc || '', // 付款方式
        paymentMethod: data[0].paymentMethod || '', // 付款方式代码
        // paymentCompanyName: data[0].paymentCompanyName || '', // 公司名称
        paymentAccountName: data[0].paymentAccountName || '',
        paymentAccount: data[0].paymentAccount || '', //
        currencyCode: data[0].currencyCode || '', // 币种
        description: data[0].description || '', // 描述
        // paymentCompanyId: data[0].paymentCompanyId || '', // 公司Id
        // billDateDesc: data[0].billDateDesc,
        bankCodeName: data[0].bankCodeName || '', // 付款银行名称
        bankCode: data[0].bankCode, // 付款银行代码
        // lineCount: data[0].lineCount, // 头下面的数据条数
        billDate: data[0].billDate.slice(0, 10), // 单据日期
        loading: false,
        money: data[0].amount, // 金额
      });
    });
  }

  /**
   * 获取开户银行列表
   */
  getAccountBank = () => {
    PaymentMaintenanceService.getAccountBank()
      .then(res => {
        if (res.data.length > 0) {
          this.setState({
            bankOptions: res.data,
          });
        }
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  /**
   * 获取付款方式值列表
   */
  getPaymentMethodOptions = () => {
    this.getSystemValueList('ZJ_PAYMENT_TYPE')
      .then(res => {
        if (res.data.values.length > 0) {
          this.setState({
            paymentMethodOptions: res.data.values,
          });
        }
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  /**
   * 付款用途值列表
   *  */
  getPaymentPurpose() {
    this.getSystemValueList('ZJ_PAYMENT_USE')
      .then(res => {
        if (res.data.values.length > 0) {
          this.setState({
            paymentPurpose: res.data.values,
          });
        }
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  }

  /*
   * 公私标志
   */
  getPublicPrivateSigns() {
    this.getSystemValueList('ZJ_BATCH_PAY_ACCOUNT_TYPE')
      .then(res => {
        if (res.data.values.length > 0) {
          this.setState({
            publicPrivateSigns: res.data.values,
          });
        }
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  }

  /**
   * 卡折标志
   */
  getDiscountSign() {
    this.getSystemValueList('ZJ_CARD_BOOK_FLAG')
      .then(res => {
        if (res.data.values.length > 0) {
          this.setState({
            discountSign: res.data.values,
          });
        }
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  }

  /**
   * 是否付款
   */
  getIfPayment() {
    this.getSystemValueList('if_payment')
      .then(res => {
        if (res.data.values.length > 0) {
          this.setState({
            ifPayment: res.data.values,
          });
        }
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  }

  /**
   * 获取开户银行列表
   */
  getBackList = id => {
    const { pagination1 } = this.state;
    this.setState({ loading: true });
    PaymentMaintenanceService.getAccountOpenMaintenanceList(
      pagination1.page,
      pagination1.pageSize,
      id
    ).then(response => {
      this.setState({
        bankList: response.data,
        loading: false,
        pagination: {
          ...pagination1,
          total: Number(response.headers['x-total-count'])
            ? Number(response.headers['x-total-count'])
            : 0,
          onChange: this.onChangePagerBand,
          current: pagination1.page + 1,
        },
      });
    });
  };

  /**
   * 银行账户选择弹框
   */
  openModal = () => {
    this.setState({
      visible: true, // 弹框显示判断
    });
    this.getBackList();
  };

  /**
   * 银行账户弹框保存关闭
   */
  onClose = () => {
    this.setState({
      visible: false, // 弹框显示判断
    });
  };

  /**
   * 银行账户弹框取消
   */
  onBack = () => {
    this.setState({
      visible: false, // 弹框显示判断
      accountNumber: '', // 带出的银行账号
      currencyCodeSelect: '', // 带出的币种
      accountName: '', // 带出收款账户
      openBank: '', // 银行Id
      openBankName: '', // 银行名称
    });
  };

  /**
   * 银行账号分页点击
   */
  onChangePagerBank = pagination1 => {
    const temp = {};
    temp.page = pagination1.current - 1;
    temp.current = pagination1.current;
    temp.pageSize = pagination1.pageSize;
    this.setState(
      {
        pagination1: temp,
      },
      () => {
        this.getBackList();
      }
    );
  };

  /**
   * 数据分页点击
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
   * 银行账户的搜索
   */
  search = e => {
    const { form } = this.props;
    e.preventDefault();
    form.validateFields((err, values) => {
      if (values.account) {
        this.getBackList(values.account);
      } else {
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
   * 新建
   */
  handleListShow = () => {
    const { tableData, dataTemp } = this.state;
    const value = {
      idSun: 1, // 删除未保存的行用到的字段
      cardSign: 'BANK_CARD',
      // sequence: 10,
      status: 'new', // 判断行是否新建
    };
    // dataTemp[tableData.length] = value;
    tableData.push(value);
    this.setState({ tableData, dataTemp });
  };

  /**
   * 提示框显示
   */
  noticeAlert = rows => {
    const noticeAlert = (
      <span>
        已选择<span style={{ fontWeight: 'bold', color: '#108EE9' }}> {rows.length} </span> 项
      </span>
    );
    this.setState({
      noticeAlert: rows.length ? noticeAlert : null,
      // batchDelete: !rows.length,
    });
  };

  /**
   * 行选择
   */
  onSelectChange = (selectedRowKeys, selectedRow) => {
    this.setState(
      {
        selectedRowKeys,
        selectedRow,
        // batchDelete: !(selectedRowKeys.length > 0),
      },
      () => {
        if (selectedRowKeys.length > 0) {
          this.noticeAlert(selectedRow);
        } else {
          this.setState({
            noticeAlert: null,
          });
        }
      }
    );
  };

  /**
   * 新建
   */
  handleSaveClick = e => {
    e.preventDefault();
    const {
      form: { getFieldsValue },
    } = this.props;
    const { tableData, idNew, billType } = this.state;
    // if(idNew || userId){
    const { match } = this.props;
    let saveDateHead = {}; // 保存的头信息
    let saveDate = {}; // 总的保存信息
    let amountNum = 0; // 总金额
    if (tableData.length > 0) {
      tableData.forEach(val => {
        amountNum += val.amount;
      });
    }
    const params = getFieldsValue();
    saveDateHead = {
      id: idNew || match.params.id || '',
      paymentAccountName: params.paymentAccountName || '', // 付款户名
      bankCode: params.bankCodeName[0] ? params.bankCodeName[0].key : '', // 银行代码
      billDate: params.billDate // 单据日期
        ? moment(params.billDate)
        : '',
      currencyCode: params.currencyCode || '', // 币种
      description: params.description || '', // 描述
      paymentAccount: params.paymentAccount || '', // 付款账号
      paymentBatchNumber: params.paymentBatchNumber || '', // 单据编号
      paymentMethod: params.paymentMethodDesc ? params.paymentMethodDesc.key : '', // 付款方式
      paymentCompanyId: params.paymentCompanyName[0] ? params.paymentCompanyName[0].id : '', // 付款公司Id
      billType: billType || 'MANUAL_PAYMENT',
      lineCount: tableData.length,
      amount: amountNum, // 总金额
    };
    // 总的保存信息
    saveDate = {
      paymentBaseInfo: saveDateHead, // 头数据
      paymentLineInfoList: tableData, // 行数据
    };
    let noSave = false;
    if (billType !== 'BATCH_PAYMENT') {
      /* eslint-disable */
      if (tableData.length > 0) {
        for (let i = 1; i <= tableData.length; i += 1) {
          console.log('---', Object.keys(tableData[i - 1]).length);
          let a = Object.keys(tableData[i - 1]).length;
          /* 必输判断 */
          if (a < 12) {
            noSave = true;
            break;
          } else if (
            tableData[i - 1].amount === null ||
            tableData[i - 1].gatherAccountName === '' ||
            tableData[i - 1].gatherAccount === '' ||
            tableData[i - 1].description === '' ||
            tableData[i - 1].gatherBranchBankName === ''
          ) {
            noSave = true;
            break;
          }
        }
      }
      /* eslint-enable */
    }
    if (
      saveDateHead.paymentAccountName === '' ||
      saveDateHead.bankCode === '' ||
      saveDateHead.billDate === '' ||
      saveDateHead.currencyCode === '' ||
      saveDateHead.paymentAccount === '' ||
      saveDateHead.paymentMethod === '' ||
      saveDateHead.paymentCompanyName === ''
    ) {
      message.error('请填写完整的头信息');
    } else if (noSave) {
      message.error('必输字段未维护');
    } else {
      PaymentMaintenanceService.saveMaintainData(saveDate)
        .then(res => {
          message.success('保存成功');
          if (match.params.id) {
            this.getList(match.params.id);
            this.getUserList(match.params.paymentBatchNumber);
          } else {
            this.setState({
              idNew: res.data.paymentBaseInfo.id,
              paymentBatchNumberNew: res.data.paymentBaseInfo.paymentBatchNumber,
            });
            this.getUserList(res.data.paymentBaseInfo.paymentBatchNumber);
            this.getList(res.data.paymentBaseInfo.id);
          }
        })
        .catch(err => {
          message.error(err.response.data.message);
        });
    }
    // }
  };

  /**
   * 展示删除弹框
   */
  showDeleteConfirm = () => {
    const aThis = this;
    if (aThis.state.selectedRowKeys) {
      confirm({
        title: '您讲删除所选择的单据，删除后单据将退回至付款工作台，是否继续?',
        okText: '确定',
        okType: 'danger',
        cancelText: '取消',
        onOk() {
          aThis.deleteItems();
        },
      });
    }
  };

  /**
   * 删除行
   */
  deleteItems = async () => {
    // 从数据库删除的数据ID集合
    const hasIdselected = [];
    // 从数据库删除成功的标志
    let deleteDateSuccessFlag = true;
    // 删除返回的result
    let result = '';
    const { selectedRow, tableData } = this.state;
    // 收集有ID的数据集合
    selectedRow.forEach(item => {
      if (item.id) {
        hasIdselected.push(item.id);
      }
    });
    // 选出删除的后的table
    const resTableData = tableData.filter(item => {
      return !selectedRow.includes(item);
    });
    // 如果选中数据有ID就执行后端删除操作
    if (hasIdselected.length > 0) {
      result = await PaymentMaintenanceService.deleteManualList(hasIdselected);
    }
    if (result) {
      if (result.status === 200) {
        deleteDateSuccessFlag = true;
      } else {
        deleteDateSuccessFlag = false;
      }
    }
    if (deleteDateSuccessFlag) {
      message.success('删除成功！');

      this.setState({
        tableData: resTableData,
        selectedRow: [],
        selectedRowKeys: [],
        noticeAlert: null,
      });
    } else {
      message.error(result.response.data.message);
    }
    // this.handleSaveClickDelete();
  };

  /**
   * 删除之后的保存
   */
  handleSaveClickDelete = () => {
    const { tableData, idNew, billType } = this.state;
    const { match } = this.props;
    let saveDateHead = {}; // 保存的头信息
    let saveDate = {}; // 总的保存信息
    let amountNum = 0; // 总金额
    if (tableData.length > 0) {
      tableData.forEach(val => {
        amountNum += val.amount;
      });
    }
    saveDateHead = {
      id: idNew || match.params.id || '',
      billType: billType || 'MANUAL_PAYMENT',
      lineCount: tableData.length,
      amount: amountNum, // 总金额
    };
    // 总的保存信息
    saveDate = {
      paymentBaseInfo: saveDateHead, // 头数据
      paymentLineInfoList: tableData, // 行数据
    };
    PaymentMaintenanceService.saveMaintainData(saveDate).then(res => {
      if (idNew) {
        this.getList(idNew);
        this.getUserList(res.data.paymentBaseInfo.paymentBatchNumber);
      } else if (match.params.id) {
        this.getList(match.params.id);
        this.getUserList(match.params.paymentBatchNumber);
      }
    });
  };

  /**
   * 返回
   */
  back = () => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/payment-management/payment-slip-maintenance/payment-slip-maintenance`,
      })
    );
  };

  /**
   * 整单删除
   */
  deleteItemsAll = () => {
    const { userId } = this.state;
    const { dispatch } = this.props;
    PaymentMaintenanceService.deleteAllManualList(userId)
      .then(res => {
        if (res.status === 200) {
          message.success('删除成功！');
          // this.getList(userId);
          dispatch(
            routerRedux.push({
              pathname: `/payment-management/payment-slip-maintenance/payment-slip-maintenance`,
            })
          );
        }
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  /**
   * 监听form控件值的改变
   * */
  handleValueChange = (value, index, dataIndex) => {
    const { tableData, idNew } = this.state;
    const { match } = this.props;
    tableData[index].paymentBaseId = idNew || match.params.id || ''; // 头id
    if (dataIndex === 'amount') {
      tableData[index].amount = value; // 金额
    } else if (dataIndex === 'paymentPurposeDesc') {
      tableData[index].paymentPurpose = value.key; // 付款用途
    } else if (dataIndex === 'propFlagDesc') {
      tableData[index].propFlag = value.key; // 公私标志
    } else if (dataIndex === 'cardSignDesc') {
      tableData[index].cardSign = value.key; // 卡折标志
    } else if (dataIndex === 'ifPaymentDesc') {
      tableData[index].ifPayment = value.key; // 是否付款
    }
  };

  /**
   * 输入框变化事件
   */
  onDescChange = (e, index, dataIndex) => {
    const { tableData } = this.state;
    if (dataIndex === 'gatherAccountName') {
      tableData[index].gatherAccountName = e.target.value; // 收款户名
    } else if (dataIndex === 'gatherBranchBankName') {
      tableData[index].gatherBranchBankName = e.target.value; // 收款分行
    } else if (dataIndex === 'gatherAccount') {
      tableData[index].gatherAccount = e.target.value; // 收款账号
    } else if (dataIndex === 'description') {
      tableData[index].description = e.target.value; // 摘要
    } else if (dataIndex === 'tradeCode') {
      tableData[index].tradeCode = e.target.value; // 单据流水号
    }
  };

  /**
   * 提交
   */
  handleCreateClick = () => {
    const { idNew, userId, allAccount, money } = this.state;
    if (idNew || userId) {
      const { dispatch } = this.props;
      if (allAccount || money) {
        PaymentMaintenanceService.submit(idNew || userId)
          .then(() => {
            message.success('提交成功');
            dispatch(
              routerRedux.push({
                pathname: `/payment-management/payment-slip-maintenance/payment-slip-maintenance`,
              })
            );
          })
          .catch(err => {
            message.error(err.response.data.message);
          });
      } else {
        message.error('金额不能为0');
      }
    } else {
      message.error('请先保存数据');
    }
  };

  render() {
    const {
      form: { getFieldDecorator },
      company,
    } = this.props;
    const {
      paymentBatchNumber,
      paymentMethodDesc,
      paymentAccountName,
      openBank,
      openBankName,
      paymentMethod,
      paymentAccount,
      currencyCode,
      description,
      bankCodeName,
      bankCode,
      billDate,
      money,
      pagination,
      columns,
      noticeAlert,
      loading,
      selectedRow,
      selectedRowKeys,
      tableData,
      columns1,
      bankList,
      visible,
      pagination1,
      accountNumber,
      currencyCodeSelect,
      accountName,
      bankOptions,
      paymentMethodOptions,
      isTrue,
      paymentBatchNumberNew,
      billType,
      allAccount,
    } = this.state;
    const rowRadioSelection = {
      type: 'radio',
      columnTitle: '选择',
      onSelect: record => {
        this.setState({
          accountNumber: record.accountNumber, // 带出的银行账号
          currencyCodeSelect: record.currencyCode, // 带出的币种
          accountName: record.accountName, // 带出收款账户
          openBank: record.openBank, // 银行Id
          openBankName: record.openBankName, // 银行名称
        });
      },
    };
    const rowSelection = {
      selectedRowKeys,
      selectedRow,
      onChange: this.onSelectChange,
    };
    const formItemLayout2 = {
      labelCol: {
        span: 3,
      },
      wrapperCol: {
        span: 4,
      },
    };
    return (
      <div className="train">
        <div className="table-header">
          <Form style={{ marginTop: '-10px' }}>
            <Row>
              <Col span={5}>
                <FormItem label="付款单号:" style={{ marginBottom: '0px' }}>
                  {getFieldDecorator('paymentBatchNumber', {
                    initialValue: paymentBatchNumberNew || paymentBatchNumber || '',
                  })(<Input disabled placeholder={paymentBatchNumber} />)}
                </FormItem>
              </Col>
              <Col span={5} offset={1}>
                <FormItem label="付款方式:" style={{ marginBottom: '0px' }}>
                  {getFieldDecorator('paymentMethodDesc', {
                    initialValue: paymentMethodDesc
                      ? [{ key: paymentMethod, label: paymentMethodDesc }]
                      : [],
                  })(
                    <Select labelInValue placeholder="请选择" allowClear>
                      {paymentMethodOptions.map(option => {
                        return <Option key={option.value}>{option.name}</Option>;
                      })}
                    </Select>
                  )}
                </FormItem>
              </Col>
              <Col span={5} offset={1}>
                <FormItem label="付款公司：:" style={{ marginBottom: '0px' }}>
                  {getFieldDecorator('paymentCompanyName', {
                    initialValue: [{ id: company.id, name: company.name }],
                  })(
                    <Chooser
                      type="company"
                      labelKey="name"
                      valueKey="id"
                      single
                      disabled
                      showClear={false}
                      listExtraParams={{ setOfBooksId: company.setOfBooksId }}
                    />
                  )}
                </FormItem>
              </Col>
              <Col span={5} offset={1}>
                <FormItem label="单据日期：" style={{ marginBottom: '0px' }}>
                  {getFieldDecorator('billDate', {
                    initialValue: moment(billDate) || '',
                  })(<DatePicker format="YYYY-MM-DD" />)}
                </FormItem>
              </Col>
            </Row>
            <Row>
              <Col span={5}>
                <FormItem label="付款账号：" style={{ marginBottom: '0px' }}>
                  {getFieldDecorator('paymentAccount', {
                    initialValue: accountNumber || (paymentAccount || ''),
                  })(<Input placeholder="请选择" onClick={this.openModal} allowClear />)}
                </FormItem>
              </Col>
              <Col span={5} offset={1}>
                <FormItem label="付款银行" style={{ marginBottom: '0px' }}>
                  {getFieldDecorator('bankCodeName', {
                    initialValue: openBank
                      ? [{ key: openBank, label: openBankName }]
                      : bankCodeName
                        ? [{ key: bankCode, label: bankCodeName }]
                        : [],
                  })(
                    <Select labelInValue placeholder="请选择" disabled>
                      {bankOptions.map(option => {
                        return <Option key={option.value}>{option.name}</Option>;
                      })}
                    </Select>
                  )}
                </FormItem>
              </Col>
              <Col span={5} offset={1}>
                <FormItem label="付款账户" style={{ marginBottom: '0px' }}>
                  {getFieldDecorator('paymentAccountName', {
                    initialValue: accountName || (paymentAccountName || ''),
                  })(<Input disabled />)}
                </FormItem>
              </Col>
              <Col span={5} offset={1}>
                <FormItem label="币种" style={{ marginBottom: '0px' }}>
                  {getFieldDecorator('currencyCode', {
                    initialValue: currencyCodeSelect || (currencyCode || ''),
                  })(<Input disabled />)}
                </FormItem>
              </Col>
            </Row>
            <Row style={{ marginTop: '-20px' }}>
              <Col span={7}>
                <FormItem label="描述：" style={{ marginBottom: '0px' }}>
                  {getFieldDecorator('description', {
                    initialValue: description || '',
                  })(<Input placeholder="请输入" allowClear />)}
                </FormItem>
              </Col>
            </Row>
          </Form>
          <Modal
            title="银行账号选择"
            visible={visible} // 是否显示
            onOk={this.onClose} // 确认
            onCancel={this.onBack} // 取消
            width="60%"
          >
            {/* 搜索区域 */}
            <Form style={{ paddingBottom: '20px' }} onSubmit={this.search}>
              <Form.Item label="银行账号" {...formItemLayout2}>
                {getFieldDecorator('account', {
                  initialValue: '',
                })(<Input autoComplete="off" onPressEnter={this.search} />)}
              </Form.Item>
              <div style={{ position: 'relative', left: '80%' }}>
                <Button type="primary" onClick={this.search}>
                  {this.$t('搜索')}
                </Button>&nbsp;&nbsp;&nbsp;
                <Button onClick={this.searchClear}>重置</Button>
              </div>
            </Form>
            <Table
              rowKey={record => record.id}
              columns={columns1}
              dataSource={bankList}
              pagination={pagination1}
              rowSelection={rowRadioSelection}
              loading={loading}
              onChange={this.onChangePagerBank}
              bordered
              size="middle"
            />
          </Modal>

          {/* 数据列表 */}
          <div className="table-header-buttons">
            <Row style={{ marginTop: '20px' }}>
              <Col>
                {/* 新建 */}
                <Button
                  type="primary"
                  onClick={() => this.handleListShow(true)}
                  disabled={billType === 'BATCH_PAYMENT'}
                >
                  {this.$t('新增行')}
                </Button>
                {/* 删除 */}
                <Button
                  type="danger"
                  onClick={e => {
                    e.preventDefault();
                    e.stopPropagation();
                    this.showDeleteConfirm();
                  }}
                >
                  {this.$t('删除行')}
                </Button>
                <Button type="primary" onClick={this.handleSaveClick}>
                  {this.$t('保存')}
                </Button>
                <Button
                  type="primary"
                  onClick={this.handleCreateClick}
                  style={{ marginLeft: '15px' }}
                >
                  {this.$t('提交')}
                </Button>
                {/* <Button type="primary" onClick={this.back} style={{ marginLeft: '15px' }}>
                  {this.$t('返回')}
                </Button> */}
                <Button
                  type="danger"
                  disabled={isTrue}
                  onClick={this.deleteItemsAll}
                  style={{ marginLeft: '15px' }}
                >
                  {this.$t('整单删除')}
                </Button>
              </Col>
            </Row>
          </div>
          {/* 提示信息 */}
          {noticeAlert ? (
            <Alert message={noticeAlert} type="info" showIcon style={{ marginBottom: '10px' }} />
          ) : (
            ''
          )}
          <Table
            rowKey={record => record.id}
            columns={columns}
            dataSource={tableData}
            pagination={pagination}
            rowSelection={rowSelection}
            loading={loading}
            onChange={this.onChangePager}
            onRowClick={this.handleRowClick}
            bordered
            size="middle"
            scroll={{ x: 1500 }}
          />
          <Row style={{ paddingBottom: '30px' }}>
            <Col>合计：{allAccount || money} 元</Col>
          </Row>
          <a style={{ fontSize: '14px', paddingBottom: '40px' }} onClick={this.back}>
            <Icon type="rollback" style={{ marginRight: '5px', paddingBottom: '15px  ' }} />返回
          </a>
        </div>
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
export default connect(map)(Form.create()(ManualMaintain));
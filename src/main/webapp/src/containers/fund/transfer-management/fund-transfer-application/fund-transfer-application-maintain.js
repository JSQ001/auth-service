import React from 'react';
import {
  Form,
  Button,
  message,
  Input,
  Alert,
  Modal,
  Row,
  Col,
  DatePicker,
  InputNumber,
  Icon,
  // Upload,
} from 'antd';
import Table from 'widget/table';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import 'styles/fund/transfer.scss';
import moment from 'moment';
// import Upload from 'upload';
import Chooser from 'widget/chooser';
import config from 'config';
import Upload from './upload.js';
import FundAccountLov from '../../fund-components/fund-account-lov';
import FundCompanyLov from '../../fund-components/fund-company-lov';
import PaymentMaintenanceService from './fund-transfer-application.service';

const { confirm } = Modal;
const FormItem = Form.Item;

class ManualMaintain extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      uploadOIDs: [], // 附件oid
      dataTemp: [], // 新增时用来push到tableData中
      tableData: [], // 数据列表
      isNew: true,
      isUpload: false, // 附件上传
      applicationInformation: {}, // 根据id查询得到的数据
      pagination: {
        total: 0, // 数据总数
        page: 0, // 用于计算页数
        pageSize: 10, // 每页条数
        current: 1, // 当前页数
        showSizeChanger: true, // 是否可以改变 pageSize
        showQuickJumper: true, // 是否可以快速跳转至某页
        showTotal: (total, range) => `显示${range[0]}-${range[1]} 共 ${total} 条`, // 用于显示数据总量和当前数据顺序
      },
      fileList: [],
      index: 0,
      columns: [
        {
          title: '序号',
          dataIndex: 'orderNumber',
          width: 50,
        },
        {
          title: '下拨日期',
          dataIndex: 'downDate',
          width: '15%',
          render: (value, record, index) => {
            const {
              form: { getFieldDecorator },
            } = this.props;
            const {
              billStatus,
              // isNew,
            } = this.state;
            return billStatus !== 'ZJ_APPROVED' ? (
              <Form>
                <FormItem>
                  {getFieldDecorator(`c${index.toString()}`, {
                    initialValue: record.downDate ? moment(record.downDate) : '',
                  })(
                    <DatePicker
                      style={{ border: '0px' }}
                      format="YYYY-MM-DD"
                      onChange={e => this.onDescChange(e, index, 'downDate')}
                    />
                  )}
                </FormItem>
              </Form>
            ) : (
              <span>{record.downDateDesc}</span>
            );
          },
        },
        {
          title: '调出公司',
          dataIndex: 'outCompanyName',
          width: '15%',
          height: 60,
          render: (value, record, index) => {
            const {
              form: { getFieldDecorator },
            } = this.props;
            const { billStatus } = this.state;
            return billStatus !== 'ZJ_APPROVED' ? (
              <Form>
                <FormItem>
                  {/* {getFieldDecorator(`a${index.toString()}`, {
                    // initialValue: isNew ? [{ id: record.inCompanyId, name: record.inCompanyName }] : '',
                    initialValue: record.inCompanyId
                      ? [{ id: record.outCompanyId, name: record.outCompanyName }]
                      : '',
                  })(
                    <Chooser
                      type="company"
                      allowClear
                      labelKey="name"
                      valueKey="id"
                      single
                      style={{border: '0px'}}
                      showClear={false}
                      onChange={e => this.onDescChange(e, index, 'outCompanyName')}
                      listExtraParams={{ setOfBooksId: props.company.setOfBooksId }}
                    />
                  )} */}
                  {getFieldDecorator(`a${index.toString()}`, {
                    // initialValue: '',
                    // initialValue:  record.inAccount ,
                    initialValue: { key: record.outCompanyId, label: record.outCompanyName },
                  })(
                    <FundCompanyLov
                      // defaultValue={{ key: record.outCompanyId, label: record.outCompanyName }}
                      // eslint-disable-next-line no-shadow
                      onChange={value => {
                        this.onChangeLovOutCompany(value, index);
                      }}
                    />
                  )}
                </FormItem>
              </Form>
            ) : (
              <span>{record.outCompanyName}</span>
            );
          },
        },
        {
          title: '调入公司',
          dataIndex: 'inCompanyName',
          width: '15%',
          render: (value, record, index) => {
            const {
              form: { getFieldDecorator },
            } = this.props;
            const { billStatus } = this.state;
            return billStatus !== 'ZJ_APPROVED' ? (
              <Form>
                <FormItem>
                  {getFieldDecorator(index.toString(), {
                    // initialValue: '',
                    // initialValue:  record.inAccount ,
                    initialValue: { key: record.inCompanyId, lable: record.inCompanyName },
                  })(
                    <FundCompanyLov
                      // defaultValue={{ key: record.inCompanyId, lable: record.inCompanyName }}
                      /* disable-eslint */
                      // eslint-disable-next-line no-shadow
                      onChange={value => {
                        this.onChangeLovCompany(value, index);
                      }}
                      /* enable-eslint */
                    />
                  )}
                </FormItem>
              </Form>
            ) : (
              <span>{record.inCompanyName}</span>
            );
          },
        },
        {
          title: '调入账号',
          dataIndex: 'inAccount',
          width: '15%',
          render: (value, record, index) => {
            const {
              form: { getFieldDecorator },
            } = this.props;
            const {
              billStatus,
              accountLovPrams,
              // isNew,
            } = this.state;
            return billStatus !== 'ZJ_APPROVED' ? (
              <Form>
                <FormItem>
                  {getFieldDecorator(`b${index.toString()}`, {
                    // initialValue: '',
                    // initialValue:  record.inAccount ,
                    initialValue: { key: record.inAccountId, lable: record.inAccount },
                  })(
                    <FundAccountLov
                      // defaultValue={{key: record.inAccountId || ''}}
                      accountLovPrams={accountLovPrams}
                      // eslint-disable-next-line no-shadow
                      onChange={(data, value) => {
                        this.onChangeAccount(data, value, index);
                      }}
                    />
                  )}
                </FormItem>
              </Form>
            ) : (
              <span>{record.inAccount}</span>
            );
          },
        },
        {
          title: '金额',
          dataIndex: 'inAmount',
          width: '10%',
          render: (value, record, index) => {
            const { billStatus } = this.state;
            return billStatus !== 'ZJ_APPROVED' ? (
              <InputNumber
                placeholder="请输入"
                allowClear
                min={1}
                defaultValue={record.amount ? record.amount : value}
                /* eslint-disable */
                onChange={value => this.handleValueChange(value, index, 'inAmount')}
                /* eslint-enable */
                style={{ border: '0px' }}
              />
            ) : (
              <span>{record.inAmount}</span>
            );
          },
        },
        {
          title: '币种',
          dataIndex: 'currency',
          width: '10%',
          render: (value, record) => {
            const { currency } = this.state;
            return <span>{record.currency || currency}</span>;
          },
        },
        {
          title: '银行摘要',
          dataIndex: 'purpose',
          width: '10%',
          render: (value, record, index) => {
            const { billStatus } = this.state;
            return billStatus !== 'ZJ_APPROVED' ? (
              <Input
                placeholder="请输入"
                // allowClear
                defaultValue={record.purpose ? record.purpose : value}
                min={10}
                onChange={e => this.onDescChange(e, index, 'purpose')}
                style={{ border: '0px' }}
              />
            ) : (
              <span>{record.purpose}</span>
            );
          },
        },
        {
          title: '资金用途',
          dataIndex: 'description',
          width: '10%',
          render: (value, record, index) => {
            const { billStatus } = this.state;
            return billStatus !== 'ZJ_APPROVED' ? (
              <Input
                // allowClear
                placeholder="请输入"
                defaultValue={record.description ? record.description : value}
                min={10}
                onChange={e => this.onDescChange(e, index, 'description')}
                style={{ border: '0px' }}
              />
            ) : (
              <span>{record.description}</span>
            );
          },
        },
        {
          title: '附件',
          dataIndex: 'attachment',
          width: 50,
          render: (value, record, index) => {
            return (
              <div>
                <a
                  onClick={event => {
                    event.stopPropagation();
                    const { attachmentOidList } = record;
                    // const {tableData} = this.state
                    // console.log(tableData);
                    const fileList = record.attachments
                      ? record.attachments.map(item => ({
                          ...item,
                          uid: item.attachmentOid,
                          name: item.fileName,
                          status: 'done',
                        }))
                      : [];
                    this.setState(
                      {
                        index,
                        uploadOIDs: attachmentOidList || [],
                        fileList,
                      },
                      () => this.upload(fileList)
                    );
                  }}
                >
                  {' '}
                  附件
                </a>
              </div>
            );
          },
        },
      ],
      columns1: [
        {
          title: '序号',
          dataIndex: 'orderNumber',
          width: 50,
        },
        {
          title: '下拨日期',
          dataIndex: 'downDateDesc',
          width: '15%',
        },
        {
          title: '调出公司',
          dataIndex: 'outCompanyName',
          width: '15%',
          height: 60,
        },
        {
          title: '调入公司',
          dataIndex: 'inCompanyName',
          width: '15%',
        },
        {
          title: '调入账号',
          dataIndex: 'inAccount',
          width: '15%',
        },
        {
          title: '金额',
          dataIndex: 'inAmount',
          width: '10%',
        },
        {
          title: '币种',
          dataIndex: 'currency',
          width: '10%',
        },
        {
          title: '银行摘要',
          dataIndex: 'purpose',
          width: '10%',
        },
        {
          title: '资金用途',
          dataIndex: 'description',
          width: '10%',
        },
        {
          title: '支付金额',
          dataIndex: 'paymentAmount',
          width: '10%',
        },
        {
          title: '付款状态',
          dataIndex: 'paymentStatusDesc',
          width: '10%',
        },
        {
          title: '付款批号',
          dataIndex: 'documentNumber',
          width: '10%',
        },
        {
          title: '付款账号',
          dataIndex: 'paymentAccount',
          width: '10%',
        },
        {
          title: '支付出纳',
          dataIndex: 'paymentCashierName',
          width: '10%',
        },
        {
          title: '关闭状态  ',
          dataIndex: 'closedStateDesc',
          width: '10%',
        },
        {
          title: '附件',
          dataIndex: 'attachment',
          width: 50,
          render: (value, record, index) => {
            return (
              <div>
                <a
                  onClick={event => {
                    event.stopPropagation();
                    const { attachmentOidList } = record;
                    // const {tableData} = this.state
                    // console.log(tableData);
                    const fileList = record.attachments
                      ? record.attachments.map(item => ({
                          ...item,
                          uid: item.attachmentOid,
                          name: item.fileName,
                          status: 'done',
                        }))
                      : [];
                    this.setState(
                      {
                        index,
                        uploadOIDs: attachmentOidList || [],
                        fileList,
                      },
                      () => this.upload(fileList)
                    );
                  }}
                >
                  {' '}
                  附件
                </a>
              </div>
            );
          },
        },
      ],
    };
  }

  componentWillMount() {
    const { match } = this.props;
    if (match.params.id === 'new') {
      // console.log('new');
      this.setState({
        styleNone: {
          marginLeft: '15px',
        },
        styleShow: {
          marginLeft: '15px',
          display: 'none',
        },
      });
    } else {
      this.setState({
        isNew: false,
        styleShow: {
          marginLeft: '15px',
          display: 'none',
        },
      });
      this.getModifyAccountDetail(match.params.id);
    }
  }

  /**
   * 根据头ID获取详情
   */
  getModifyAccountDetail = id => {
    this.setState({ loading: true });
    PaymentMaintenanceService.getModifyAccountDetail(id).then(res => {
      if (res.data.headerDTO.billStatus === 'ZJ_APPROVED') {
        this.setState({
          billStatus: res.data.headerDTO.billStatus,
          styleNone: {
            marginLeft: '15px',
            display: 'none',
          },
          styleShow: {
            marginLeft: '15px',
            display: 'block',
          },
        });
      }
      this.setState({
        loading: false,
        allAccount: res.data.headerDTO.amount,
        applicationInformation: res.data.headerDTO,
        tableData: res.data.listResponseEntity.body,
        applyNumber: res.data.listResponseEntity.body.applyNumber,
        versionNumber: res.data.headerDTO.versionNumber,
      });
    });
  };

  /**
   * 调出公司
   */
  onChangeLovOutCompany = (value, index) => {
    const { tableData } = this.state;
    tableData[index].outCompanyId = value.key; // 调入公司
    this.setState({ tableData });
  };

  /**
   * 调入公司
   */
  onChangeLovCompany = (value, index) => {
    const { tableData } = this.state;
    tableData[index].inCompanyId = value.key; // 调入公司
    tableData[index].inAccountId = '';
    const accountLovPrams = {
      companyId: value.key,
    };
    this.setState({ accountLovPrams, tableData });
  };

  /**
   * 调入账号
   */
  onChangeAccount = (data, value, index) => {
    const { tableData } = this.state;
    tableData[index].inAccountId = data.key; // 调入账号
    tableData[index].currency = value.currencyCode || '';
    this.setState({
      tableData,
    });
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
   * 新增行
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
   * 附件上传
   */
  upload = fileList => {
    this.setState({
      fileList,
      isUpload: true,
    });
  };

  /**
   * 附件上传关闭关闭
   */
  onClose = () => {
    this.setState({
      isUpload: false,
    });
  };

  /**
   * 附件上传取消
   */
  onBack = () => {
    this.setState({
      isUpload: false,
    });
  };

  /**
   * 上传附件
   */
  handleUpload = OIDs => {
    // console.log(OIDs)
    const { uploadOIDs, index, tableData } = this.state;
    OIDs.forEach(item => {
      uploadOIDs.push(item);
    });
    tableData[index].attachment = uploadOIDs.join(',');
    this.setState({
      tableData,
    });
  };

  /**
   * 保存
   */
  handleSaveClick = e => {
    e.preventDefault();
    const {
      form: { getFieldsValue },
      user,
    } = this.props;
    const { tableData, idNew, versionNumber } = this.state;
    const { match } = this.props;
    let saveDateHead = {}; // 保存的头信息
    let saveDate = {}; // 总的保存信息
    let amountNum = 0; // 总金额
    if (tableData.length > 0) {
      tableData.forEach(val => {
        amountNum += val.inAmount;
      });
    }
    const params = getFieldsValue();
    saveDateHead = {
      id: idNew || (match.params.id === 'new' ? '' : match.params.id),
      companyId: params.paymentCompanyName ? params.paymentCompanyName[0].id : '', // 申请公司
      departmentId: params.department ? params.department[0].departmentId : '', // 申请部门
      billDate: params.billDate // 申请日期
        ? moment(params.billDate)
        : '',
      description: params.description || '', // 描述
      employeeId: params.employeeId ? user.id : '', // 员工
      lineCount: tableData.length,
      amount: amountNum || '0', // 总金额
      versionNumber,
    };
    // 总的保存信息
    saveDate = {
      header: saveDateHead, // 头数据
      lineList: tableData, // 行数据
    };
    let noSave = false;
    // if (billType !== 'BATCH_PAYMENT') {
    /* eslint-disable */
    if (tableData.length > 0) {
      for (let i = 1; i <= tableData.length; i += 1) {
        console.log('---', Object.keys(tableData[i - 1]).length);
        let a = Object.keys(tableData[i - 1]).length;
        /* 必输判断 */
        if (a < 4) {
          noSave = true;
          break;
        } else if (
          tableData[i - 1].inAmount === (null || '') ||
          tableData[i - 1].inCompanyId === (null || '') ||
          tableData[i - 1].outCompanyId === (null || '') ||
          tableData[i - 1].purpose === (null || '') ||
          tableData[i - 1].downDate === (null || '') ||
          tableData[i - 1].currency === (null || '') ||
          tableData[i - 1].description === (null || '') ||
          tableData[i - 1].inAccountId === (null || '')
        ) {
          noSave = true;
          break;
        } else if (a < 11) {
          noSave = true;
          break;
        }
      }
    }

    // }
    if (
      saveDateHead.companyId === '' ||
      saveDateHead.departmentId === '' ||
      saveDateHead.billDate === ''
    ) {
      message.error('请填写完整的头信息');
    } else if (noSave) {
      message.error('必输字段未不能为空');
    } else {
      console.log(saveDate);
      /* eslint-enable */
      PaymentMaintenanceService.createOrUpdate(saveDate)
        .then(res => {
          message.success('保存成功');
          if (match.params.id !== 'new') {
            this.getModifyAccountDetail(match.params.id);
          } else {
            this.setState({
              idNew: res.data.header.id,
            });
            this.getModifyAccountDetail(res.data.header.id);
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
        title: '确认删除?',
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
    this.handleSaveClickDelete();
  };

  /**
   * 删除之后的保存
   */
  handleSaveClickDelete = () => {
    const { tableData, idNew, versionNumber } = this.state;
    const { match } = this.props;
    let saveDateHead = {}; // 保存的头信息
    let saveDate = {}; // 总的保存信息
    let amountNum = 0; // 总金额
    if (tableData.length > 0) {
      tableData.forEach(val => {
        amountNum += val.inAmount;
      });
    }
    saveDateHead = {
      id: idNew || match.params.id,
      amount: amountNum || '0',
      lineCount: tableData.length,
      versionNumber,
    };
    // 总的保存信息
    saveDate = {
      header: saveDateHead, // 头数据
      lineList: tableData, // 行数据
    };
    PaymentMaintenanceService.createOrUpdate(saveDate).then(() => {
      this.getModifyAccountDetail(idNew || match.params.id);
    });
  };

  /**
   * 返回
   */
  back = () => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/transfer-managementfund-transfer-application/fund-transfer-application`,
      })
    );
  };

  /**
   * 监听form控件值的改变(InputNumber)
   * */
  handleValueChange = (value, index, dataIndex) => {
    const { tableData } = this.state;
    // console.log(value)
    if (dataIndex === 'inAmount') {
      tableData[index].inAmount = value; // 金额
    }
  };

  /**
   * 输入框变化事件
   */
  onDescChange = (e, index, dataIndex) => {
    const { tableData } = this.state;
    if (dataIndex === 'outCompanyName') {
      tableData[index].outCompanyId = e[0].id; // 调出公司
    } else if (dataIndex === 'inCompanyName') {
      tableData[index].inCompanyId = e[0].id; // 调入公司
    } else if (dataIndex === 'inAccount') {
      tableData[index].inAccountId = e.id; // 调入账号
      tableData[index].currency = e.currencyCode; // 币种
      this.setState({
        currency: e.currencyCode,
      });
    } else if (dataIndex === 'description') {
      tableData[index].description = e.target.value; // 摘要
    } else if (dataIndex === 'purpose') {
      tableData[index].purpose = e.target.value; // 摘要
    } else if (dataIndex === 'downDate') {
      tableData[index].downDate = moment(e); // 下拨日期
    }
  };

  /**
   * 提交
   */
  handleSubmit = () => {
    const { idNew, allAccount } = this.state;
    const { match } = this.props;
    if (idNew || match.params.id !== 'new') {
      const { dispatch } = this.props;
      if (allAccount) {
        PaymentMaintenanceService.submit(idNew || match.params.id)
          .then(() => {
            message.success('提交成功');
            dispatch(
              routerRedux.push({
                pathname: `/transfer-managementfund-transfer-application/fund-transfer-application`,
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

  /**
   * 已审批单据关闭
   */
  close = () => {
    const { selectedRowKeys } = this.state;
    const { match } = this.props;
    PaymentMaintenanceService.close(selectedRowKeys[0])
      .then(res => {
        if (res.status === 200) {
          message.success('关闭成功！');
          this.getModifyAccountDetail(match.params.id);
        }
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  render() {
    const {
      form: { getFieldDecorator },
      company,
      user,
    } = this.props;
    const {
      description,
      billDate,
      pagination,
      columns,
      noticeAlert,
      loading,
      selectedRow,
      selectedRowKeys,
      tableData,
      applyNumber,
      isNew,
      applicationInformation,
      styleNone,
      styleShow,
      isUpload,
      fileList,
      index,
      billStatus,
      columns1,
    } = this.state;
    const rowSelection = {
      selectedRowKeys,
      selectedRow,
      onChange: this.onSelectChange,
    };
    const rowSelection1 = {
      type: 'radio',
      selectedRowKeys,
      selectedRow,
      onChange: this.onSelectChange,
    };
    return (
      <div className="train">
        <Modal title="附件上传" visible={isUpload} onOk={this.onClose} onCancel={this.onBack}>
          <Upload
            disabled={billStatus === 'ZJ_APPROVED'}
            attachmentType="CONTRACT"
            uploadUrl={`${config.baseUrl}/api/upload/static/attachment`}
            fileNum={9}
            uploadHandle={this.handleUpload}
            defaultFileList={fileList}
            defaultOIDs={(tableData[index] && tableData[index].attachmentOidList) || []}
          />
        </Modal>
        <div className="table-header" style={{ background: '#FAFAFA', padding: '20px' }}>
          <Form>
            <Row>
              <Col span={5} offset={1}>
                <FormItem label="单据编号:" style={{ marginBottom: '0px' }}>
                  {getFieldDecorator('applyNumber', {
                    initialValue: applyNumber || applicationInformation.applyNumber || '',
                  })(<Input disabled />)}
                </FormItem>
              </Col>

              <Col span={5} offset={1}>
                <FormItem label="申请公司" style={{ marginBottom: '0px' }}>
                  {getFieldDecorator('paymentCompanyName', {
                    initialValue: isNew
                      ? [{ id: company.id, name: company.name }]
                      : [
                          {
                            id: applicationInformation.companyId,
                            name: applicationInformation.companyName,
                          },
                        ],
                  })(
                    <Chooser
                      disabled={billStatus === 'ZJ_APPROVED'}
                      type="company"
                      labelKey="name"
                      valueKey="id"
                      single
                      showClear={false}
                      listExtraParams={{ setOfBooksId: company.setOfBooksId }}
                    />
                  )}
                </FormItem>
              </Col>
              <Col span={5} offset={1}>
                <FormItem label="申请部门">
                  {getFieldDecorator('department', {
                    rules: [{ required: true, message: this.$t('common.please.select') }],
                    initialValue: isNew
                      ? [{ departmentId: user.departmentId, path: user.departmentName }]
                      : [
                          {
                            departmentId: applicationInformation.departmentId,
                            path: applicationInformation.departmentName,
                          },
                        ],
                  })(
                    <Chooser
                      type="department_document"
                      labelKey="path"
                      disabled={billStatus === 'ZJ_APPROVED'}
                      valueKey="departmentId"
                      single
                      showClear={false}
                      listExtraParams={{ tenantId: user.tenantId }}
                    />
                  )}
                </FormItem>
              </Col>
              <Col span={5} offset={1}>
                <FormItem label="申请人" style={{ marginBottom: '0px' }}>
                  {getFieldDecorator('employeeId', {
                    initialValue: isNew ? user.userName : applicationInformation.employeeName,
                  })(<Input disabled />)}
                </FormItem>
              </Col>
            </Row>
            <Row>
              <Col span={5} offset={1}>
                <FormItem label="申请日期" style={{ marginBottom: '0px' }}>
                  {getFieldDecorator('billDate', {
                    initialValue: isNew
                      ? moment(billDate) || ''
                      : moment(applicationInformation.billDate) || '',
                  })(<DatePicker format="YYYY-MM-DD" disabled={billStatus === 'ZJ_APPROVED'} />)}
                </FormItem>
              </Col>
              <Col span={8} offset={1}>
                <FormItem label="说明" style={{ marginBottom: '0px' }}>
                  {getFieldDecorator('description', {
                    initialValue: isNew
                      ? description || ''
                      : applicationInformation.description || '',
                  })(<Input placeholder="请输入" disabled={billStatus === 'ZJ_APPROVED'} />)}
                </FormItem>
              </Col>
            </Row>
          </Form>
        </div>
        {/* 数据列表 */}
        <div className="table-header-buttons" style={{ paddingBottom: '10px' }}>
          <Row style={{ marginTop: '20px' }}>
            <Col span={1}>
              {/* 新建 */}
              <Button type="primary" onClick={() => this.handleListShow(true)} style={styleNone}>
                {this.$t('新增行')}
              </Button>
            </Col>
            <Col offset={1} span={1}>
              {/* 删除 */}
              <Button
                type="danger"
                style={styleNone}
                onClick={e => {
                  e.preventDefault();
                  e.stopPropagation();
                  this.showDeleteConfirm();
                }}
              >
                {this.$t('删除行')}
              </Button>
            </Col>
            <Col offset={1} span={1}>
              <Button type="primary" style={styleNone} onClick={this.handleSaveClick}>
                {this.$t('保存')}
              </Button>
            </Col>
            <Col offset={1} span={1}>
              <Button type="primary" onClick={this.handleSubmit} style={styleNone}>
                {this.$t('提交')}
              </Button>
            </Col>
            <Col offset={1} span={1}>
              <Button type="primary" style={styleShow} onClick={this.close}>
                {this.$t('关闭')}
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
        <div className="tableHeight">
          <Table
            rowKey={record => record.id}
            columns={billStatus === 'ZJ_APPROVED' ? columns1 : columns}
            dataSource={tableData}
            pagination={pagination}
            rowSelection={billStatus === 'ZJ_APPROVED' ? rowSelection1 : rowSelection}
            loading={loading}
            onChange={this.onChangePager}
            // onRowClick={this.handleRowClick}
            bordered
            size="middle"
            scroll={{ x: 1500 }}
          />
        </div>
        <a style={{ fontSize: '14px', paddingBottom: '40px' }} onClick={this.back}>
          <Icon type="rollback" style={{ marginRight: '5px', paddingBottom: '15px  ' }} />返回
        </a>
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

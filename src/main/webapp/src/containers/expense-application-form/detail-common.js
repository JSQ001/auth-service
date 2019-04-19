import React from 'react';
import config from 'config';
import httpFetch from 'share/httpFetch';
import { connect } from 'dva';
import {
  Form,
  Button,
  Row,
  Col,
  Breadcrumb,
  message,
  Divider,
  Card,
  Popover,
  Modal,
  Icon,
  Dropdown,
  Menu,
  Tag,
  Popconfirm,
} from 'antd';
import Table from 'widget/table';
import SlideFrame from 'widget/slide-frame';
import { routerRedux } from 'dva/router';
import DocumentBasicInfo from 'widget/Template/document-basic-info';
import moment from 'moment';
import PropTypes from 'prop-types';
import NewApplicationLine from './new-application-line';
import ApplicationLineDetail from './application-line-detail';
import ContractDetail from 'containers/contract/contract-approve/contract-detail-common';

import ApproveHistory from 'widget/Template/approve-history-work-flow';

import service from './service';

import 'styles/pre-payment/my-pre-payment/pre-payment-detail.scss';

import prePaymentService from 'containers/pre-payment/my-pre-payment/me-pre-payment.service';
import NewPrePaymentDetail from 'containers/expense-application-form/new-pre-payment-detail';
import prePaymentTypeService from 'containers/pre-payment/prepayment-type/pre-payment-type.service';

class PrePaymentCommon extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      lineLoading: false,
      historyLoading: false, //控制审批历史记录是否loading
      columns: [
        {
          title: this.$t('expense.the.serial.number') /*序号*/,
          dataIndex: 'number',
          width: 90,
          render: (value, record, index) =>
            (this.state.pagination.current - 1) * this.state.pagination.pageSize + index + 1,
        },
        {
          title: this.$t('expense.the.company') /*公司*/,
          dataIndex: 'companyName',
          width: 150,
          render: value => {
            return <Popover content={value}>{value}</Popover>;
          },
        },
        {
          title: this.$t('expense.department') /*部门*/,
          dataIndex: 'departmentName',
          width: 150,
          render: value => {
            return <Popover content={value}>{value}</Popover>;
          },
        },
        {
          title: this.$t('expense.responsibility.center') /*责任中心*/,
          dataIndex: 'responsibilityCenterCodeName',
          width: 150,
          render: value => {
            return <Popover content={value}>{value}</Popover>;
          },
        },
        {
          title: this.$t('expense.application.type') /*申请类型*/,
          dataIndex: 'expenseTypeName',
          width: 150,
          render: value => {
            return <Popover content={value}>{value}</Popover>;
          },
        },
        {
          title: this.$t('expense.apply.amount') /*申请金额*/,
          dataIndex: 'amount',
          width: 120,
          render: value => this.filterMoney(value),
        },
        {
          title: this.$t('expense.functional.currency.amount') /*本位币金额*/,
          dataIndex: 'functionalAmount',
          width: 120,
          render: value => this.filterMoney(value),
        },
      ],
      showLineDetail: false,
      showSlideFrame: false,
      slideFrameTitle: '',
      record: {},
      approveHistory: [],
      headerInfo: {},
      backLoadding: false,
      lineInfo: {},
      showContract: false,
      pagination: {
        current: 1,
        showSizeChanger: true,
        pageSize: 5,
        pageSizeOptions: ['5', '10', '20', '50', '100'],
        showTotal: (total, range) =>
          this.$t('common.show.total', {
            range0: `${range[0]}`,
            range1: `${range[1]}`,
            total: total,
          }),
      },

      menus: [],
      //预付款单类型集合
      prePaymentTypeMenu: [],
      amountText: '',
      data: [],
      //“发起预付款”显示标志
      initiatePrepayment: true,
      //“付款信息”card显示
      paymentInformation: false,
      //选择的预付款单类型
      prePaymentType: {},
      //新增的预付款单头信息
      prePaymentHead: {},
      //付款信息侧滑框是否显示标志
      flag: false,
      //预付款行侧滑展示变量
      showPrePaymentLineSlideFrame: false,
      planLoading: false,
      prePayPagination: {
        total: 0,
        showQuickJumper: true,
        showSizeChanger: true,
      },
      pageSize: 5,
      page: 0,
      prePaymentColumns: [
        {
          title: '序号',
          dataIndex: 'index',
          align: 'center',
          width: 50,
          render: (value, record, index) => index + this.state.indexAdd + 1,
        },
        {
          title: '预付款金额',
          dataIndex: 'amount',
          width: 120,
          align: 'center',
          render: (value, record) => {
            return (
              <span className="money-cell">
                {record.currency + '  ' + record.amount.toFixed(2)}
              </span>
            );
          },
        },
        {
          title: '预付款类型',
          width: 110,
          dataIndex: 'cshTransactionClassName',
          align: 'center',
          render: cshTransactionClassName => {
            return <Popover content={cshTransactionClassName}>{cshTransactionClassName}</Popover>;
          },
        },
        {
          title: '收款对象',
          width: 80,
          dataIndex: 'currency',
          align: 'center',
          render: (value, record) => {
            return (
              <div>
                <Tag color="#000">{record.partnerCategory == 'EMPLOYEE' ? '员工' : '供应商'}</Tag>
                <div style={{ whiteSpace: 'normal' }}>{record.partnerName}</div>
              </div>
            );
          },
        },
        {
          title: '收款账户',
          width: 210,
          align: 'center',
          dataIndex: 'partnerId',
          render: (value, record) => {
            return (
              <div>
                <div>户名：{record.accountName}</div>
                <div>账户：{record.accountNumber}</div>
              </div>
            );
          },
        },
        {
          title: '付款属性',
          align: 'center',
          dataIndex: 'refDocumentCode',
          width: 200,
          render: (value, record) => {
            return (
              <Popover
                content={
                  <div style={{ whiteSpace: 'normal' }}>
                    <div>
                      计划付款日期：{record.requisitionPaymentDate
                        ? moment(record.requisitionPaymentDate).format('YYYY-MM-DD')
                        : ''}
                    </div>
                    <div>付款方式类型：{record.paymentMethodName}</div>
                  </div>
                }
              >
                <div style={{ whiteSpace: 'normal' }}>
                  <div>
                    计划付款日期：{record.requisitionPaymentDate
                      ? moment(record.requisitionPaymentDate).format('YYYY-MM-DD')
                      : ''}
                  </div>
                  <div>付款方式类型：{record.paymentMethodName}</div>
                </div>
              </Popover>
            );
          },
        },
        {
          title: '备注',
          dataIndex: 'description',
          align: 'center',
          render: value => {
            return value ? (
              <Popover placement="topLeft" content={value} overlayStyle={{ maxWidth: 300 }}>
                {value}
              </Popover>
            ) : (
              '-'
            );
          },
        },
      ],
    };
  }

  componentDidMount() {
    //设置基本信息
    this.setBasicInfo();
    //设置表格动态列
    this.setTableColumns();
    //详情页一进来，查询预付款单行
    this.getPrepayLines();
    //获取预付款单类型
    this.props.headerData.prePaymentFlag && this.getPrePaymentType();
  }

  //设置基本信息
  setBasicInfo = () => {
    const { headerData } = this.props;
    let headerInfo = {
      businessCode: headerData.documentNumber,
      createdDate: headerData.requisitionDate,
      formName: headerData.typeName,
      createByName: headerData.createdName,
      currencyCode: headerData.currencyCode,
      totalAmount: headerData.totalFunctionAmount,
      statusCode: headerData.status,
      remark: headerData.remarks,
      infoList: [
        { label: this.$t('expense.reverse.apply.name'), value: headerData.employeeName } /*申请人*/,
        { label: this.$t('expense.the.company'), value: headerData.companyName } /*公司*/,
        { label: this.$t('expense.department'), value: headerData.departmentName } /*部门*/,
      ],
      customList: headerData.dimensions
        ? headerData.dimensions
            .filter(o => o.headerFlag)
            .map(o => ({ label: o.name, value: o.valueName }))
        : [],
      attachments: headerData.attachments,
    };

    if (headerData.associateContract) {
      headerInfo.infoList.push({
        label: this.$t('expense.associated.with.the.contract') /*关联合同*/,
        value: headerData.contractNumber,
        linkId: headerData.contractHeaderId,
        onClick: id => {
          this.setState({ showContract: true });
        },
      });
    }
    this.setState({ headerInfo });
  };

  //设置表格动态列
  setTableColumns = () => {
    const { headerData } = this.props;
    let { columns, prePaymentColumns } = this.state;

    service.getColumnInfo(headerData.id).then(res => {
      res.data.map(item => {
        columns.push({
          ...item,
          width: 150,
          render: value => {
            return <Popover content={value}>{value}</Popover>;
          },
        });
      });

      let option = {
        title: this.$t('expense.reverse.remark') /*备注*/,
        dataIndex: 'remarks',
        render: value => {
          return <Popover content={value}>{value}</Popover>;
        },
      };
      if (res.data.length) {
        option.width = 350;
      }
      columns.push(option);

      //如果审批中
      if (headerData.status === 1001 || headerData.status === 1003 || headerData.status === 1005) {
        columns.push({
          title: this.$t('expense.operation') /*操作*/,
          dataIndex: 'options',
          width: 120,
          fixed: 'right',
          render: (value, record) => (
            <span>
              <a onClick={() => this.editItem(record)}>{this.$t('expense.the.editor')}</a>
              {/*编辑*/}
              <Divider type="vertical" />
              <a onClick={() => this.deleteLine(record)}>{this.$t('expense.deleted')}</a>
              {/*删除*/}
            </span>
          ),
        });
      } else {
        columns.push({
          title: this.$t('expense.operation') /*操作*/,
          dataIndex: 'options',
          width: 120,
          fixed: 'right',
          render: (value, record) => (
            <a onClick={() => this.lineDetail(record)}>{this.$t('expense.wallet.checkDetail')}</a>
          ) /*查看详情*/,
        });
      }

      this.setState({ columns }, () => {
        this.getLineInfo();
      });
    });

    if (headerData.status === 1001 || headerData.status === 1003 || headerData.status === 1005) {
      prePaymentColumns.splice(prePaymentColumns.length, 0, {
        title: '操作',
        dataIndex: 'id',
        width: 100,
        align: 'center',
        render: (text, record) => (
          <span>
            <a onClick={e => this.editPrePaymentLine(e, record)}>编辑</a>
            <span className="ant-divider" />
            <Popconfirm title="确认删除吗？" onConfirm={e => this.deletePrePaymentLine(e, record)}>
              <a>删除</a>
            </Popconfirm>
          </span>
        ),
      });
    }
  };

  //详情页一进来，查询预付款单行
  getPrepayLines = () => {
    const { headerData } = this.props;
    const { page, pageSize } = this.state;
    this.setState({ planLoading: true });

    let headParams = {
      applicationHeadId: headerData.id,
    };
    let params = {
      refDocumentId: headerData.id,
      size: pageSize,
      page: page,
    };

    prePaymentService
      .getPrepayHeadByApplicationHeaderId(headParams)
      .then(res => {
        if (res.status === 200) {
          this.setState({
            prePaymentHead: res.data,
          });
          if (res.data.id !== '') {
            this.setState({
              //此时，显示预付款单行卡片，隐藏“发起预付款”按钮
              initiatePrepayment: false,
              paymentInformation: true,
            });
          }
        }
      })
      .catch(e => {
        message.error('获取预付款单头失败');
        console.log(`获取预付款单头失败：${e.response.data}`);
      });

    prePaymentService
      .getPrepayLines(params)
      .then(res => {
        this.setState({
          data: res.data,

          planLoading: false,
          indexAdd: page * pageSize,
          prePayPagination: {
            total: Number(res.headers['x-total-count']) ? Number(res.headers['x-total-count']) : 0,
            current: page + 1,
            onChange: this.onChangePaper,
            onShowSizeChange: this.onShowSizeChange,
            pageSize: pageSize,
            showTotal: (total, range) =>
              this.$t('common.show.total', {
                range0: `${range[0]}`,
                range1: `${range[1]}`,
                total: total,
              }),
            showQuickJumper: true,
            showSizeChanger: true,
            pageSizeOptions: ['5', '10', '20', '30', '40'],
          },
        });
        if (res.data.length > 0) {
          this.setState({
            //此时，显示预付款单行卡片，隐藏“发起预付款”按钮
            initiatePrepayment: false,
            paymentInformation: true,
          });
        }
      })
      .catch(e => {
        console.log(e);
        message.error('付款信息数据加载失败，请重试');
        this.setState({ historyLoading: false });
      });
  };
  //设置预付款单类型
  getPrePaymentType = () => {
    let params = {
      userId: this.props.user.id,
      isEnabled: true,
    };
    prePaymentService
      .getPrePaymentType(params)
      .then(res => {
        if (res.status === 200) {
          this.setState({
            prePaymentTypeMenu: res.data,
          });
        }
      })
      .catch(e => {
        message.error('获取预付款单类型失败');
        /*console.log(`获取预付款单类型失败：${e.response.data}`);*/
      });
  };

  //获取行数据
  getLineInfo = () => {
    const { headerData } = this.props;
    const {
      pagination: { pageSize, current },
      pagination,
    } = this.state;
    this.setState({ lineLoading: true });
    service
      .getApplicationLines(headerData.id, { size: pageSize, page: current - 1 })
      .then(res => {
        let { headerInfo } = this.state;
        headerInfo.totalAmount = res.data.currencyAmount ? res.data.currencyAmount.amount : '0.00';
        this.setState({
          headerInfo,
          lineInfo: res.data,
          lineLoading: false,
          pagination: { ...pagination, total: Number(res.headers['x-total-count']) },
        });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  //删除行数据
  deleteLine = ({ id }) => {
    service
      .deleteLine(id)
      .then(res => {
        message.success(this.$t('expense.delete.successfully1')); // 删除成功！
        let { pagination } = this.state;
        pagination.current = 1;
        this.setState({ pagination }, () => {
          this.getLineInfo();
        });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  //侧滑
  showSlide = flag => {
    this.setState({ showSlideFrame: flag, record: {} });
  };

  //关闭侧滑
  handleCloseSlide = flag => {
    let { pagination } = this.state;
    this.setState({ showSlideFrame: false, flag: false, record: {} }, () => {
      if (flag) {
        pagination.current = 1;
        this.setState({ pagination }, this.getLineInfo);
      }
    });
  };

  //编辑
  edit = () => {
    this.props.dispatch(
      routerRedux.push({
        pathname:
          `/expense-application/expense-application/edit-expense-application/` +
          this.props.headerData.id,
      })
    );
  };

  //取消
  onCancel = () => {
    this.props.dispatch(
      routerRedux.push({
        pathname: '/expense-application/expense-application/expense-application-form',
      })
    );
  };

  //撤销
  back = () => {
    let params = {
      entities: [
        {
          entityOid: this.props.headerData.documentOid,
          entityType: 801009,
        },
      ],
    };
    this.setState({ backLoadding: true });
    service
      .withdraw(params)
      .then(res => {
        this.setState({ backLoadding: false });
        message.success(this.$t('exp.withDraw.success')); // 撤回成功！
        this.onCancel();
      })
      .catch(err => {
        this.setState({ backLoadding: false });
        message.error(err.response.data.message);
      });
  };

  //添加行信息
  addItem = () => {
    this.setState({
      showSlideFrame: true,
      slideFrameTitle: this.$t('expense.new.application.for.a.single'),
    }); /*新建申请单行*/
  };

  //编辑行信息
  editItem = record => {
    this.setState({ record }, () => {
      this.setState({
        showSlideFrame: true,
        slideFrameTitle: this.$t('expense.editor.for.special'),
      }); /*编辑申请单行*/
    });
  };

  //查看行详情
  lineDetail = record => {
    this.setState({ record }, () => {
      this.setState({
        showLineDetail: true,
        slideFrameTitle: this.$t('expense.editor.for.special'),
      }); /*编辑申请单行*/
    });
  };

  //扩展行
  expandedRow = record => {
    if (record.currencyCode == this.props.company.baseCurrency) return null;
    return (
      <div>
        <Row>
          <Col span={2}>
            <span style={{ float: 'right' }}>{this.$t('expense.amount.of.property')}</span>
            {/*金额属性*/}
          </Col>
          <Col span={6} offset={1}>
            {this.$t('expense.exchange.rate.date1')}
            {moment(record.exchangeDate).format('YYYY-MM-DD')}
          </Col>
          <Col span={6}>
            {this.$t('expense.exchange.rate1')}
            {record.exchangeRate}
          </Col>
        </Row>
      </div>
    );
  };

  tableChange = pagination => {
    this.setState({ pagination }, () => {
      this.getLineInfo();
    });
  };

  /**
   * 获取付款信息 数据
   */
  getList = () => {
    const { prePaymentHead, page, pageSize } = this.state;
    this.setState({ planLoading: true });

    let params = {
      headId: prePaymentHead.id,
      size: pageSize,
      page: page,
    };

    prePaymentService
      .getLineByHeadId(params)
      .then(res => {
        let headerData = this.state.headerData;
        this.setState({
          data: res.data,
          planLoading: false,
          indexAdd: page * pageSize,
          prePayPagination: {
            total: Number(res.headers['x-total-count']) ? Number(res.headers['x-total-count']) : 0,
            current: page + 1,
            onChange: this.onChangePaper,
            onShowSizeChange: this.onShowSizeChange,
            pageSize: pageSize,
            showTotal: (total, range) =>
              this.$t('common.show.total', {
                range0: `${range[0]}`,
                range1: `${range[1]}`,
                total: total,
              }),
            showQuickJumper: true,
            showSizeChanger: true,
            pageSizeOptions: ['5', '10', '20', '30', '40'],
          },
        });
      })
      .catch(e => {
        console.log(e);
        message.error('付款信息数据加载失败，请重试');
        this.setState({ historyLoading: false });
      });
  };
  onChangePaper = page => {
    let prePayPagination = this.state.prePayPagination;
    prePayPagination.current = page;
    this.setState({ page: page - 1, prePayPagination }, this.getList);
  };
  /**
   * 切换每页显示的条数
   */
  onShowSizeChange = (current, pageSize) => {
    let prePayPagination = this.state.prePayPagination;
    prePayPagination.current = 1;
    prePayPagination.pageSize = pageSize;
    this.setState(
      {
        page: 0,
        pageSize: pageSize,
        prePayPagination,
      },
      () => {
        this.getList();
      }
    );
  };
  //点击“发起预付款”，选择一个预付款单类型后，就新增一个预付款单头
  insertPrePaymentHead = param => {
    const { headerData } = this.props;
    //获取所选择的预付款单类型字段数据
    // const {prePaymentType} = this.state;
    let dataValue;
    prePaymentTypeService
      .getPrePaymentTypeById(param.key)
      .then(res => {
        this.setState({
          prePaymentType: res.data.cashPayRequisitionType,
        });
        dataValue = {
          paymentReqTypeId: param.key,
          tenantId: headerData.tenantId,
          employeeId: headerData.employeeId,
          companyId: headerData.companyId,
          unitId: headerData.departmentId,
          description: headerData.remarks,
          reqIn: true,
          refDocumentId: headerData.id,
          // ifWorkflow: prePaymentType.formOid != 0,
          // formOid: prePaymentType.formOid != 0 ? prePaymentType.formOid : '',
          ifWorkflow: res.data.cashPayRequisitionType.formOid != 0,
          formOid: res.data.cashPayRequisitionType.formOid,
        };

        prePaymentService
          .addPrepaymentHead(dataValue)
          .then(res => {
            this.setState({
              loading: false,
              initiatePrepayment: false,
              paymentInformation: true,
            });

            if (res.data.id !== '') {
              prePaymentService.getHeadById(res.data.id).then(res => {
                this.setState({ prePaymentHead: res.data });
              });
            }
          })
          .catch(e => {
            message.error(`保存失败，${e.response.data.message}`);
            this.setState({
              loading: false,
            });
          });
      })
      .catch(e => {
        message.error(`查询所选预付款单类型失败，${e.response.data.message}`);
      });
  };
  //点击“新建付款信息”，添加预付款行信息
  addPrePaymentLine = () => {
    const { headerData } = this.props;
    const { prePaymentHead } = this.state;
    console.log(headerData);
    console.log(prePaymentHead);
    this.setState({
      record: {
        payMethodsType: prePaymentHead.paymentMethod,
        isApply: prePaymentHead.ifApplication,
        paymentMethodCode: prePaymentHead.paymentMethodCode,
        refDocumentId: headerData.id,
        refDocumentCode: headerData.documentNumber,
      },
      slideFrameTitle: '新建付款信息',
      id: prePaymentHead.id,
      companyId: prePaymentHead.companyId,
      flag: true,
      showPrePaymentLineSlideFrame: true,
    });
  };
  //编辑预付款行信息
  editPrePaymentLine = (e, record) => {
    e.preventDefault();
    this.setState({
      record: {
        ...record,
        payMethodsType: this.state.prePaymentHead.paymentMethod,
        isApply: this.state.prePaymentHead.ifApplication,
        paymentMethodCode: this.state.prePaymentHead.paymentMethodCode,
      },
      slideFrameTitle: '编辑付款计划',
      id: this.state.prePaymentHead.id,
      companyId: this.state.prePaymentHead.companyId,
      paymentReqTypeId: this.state.prePaymentHead.paymentReqTypeId,
      flag: true,
      showPrePaymentLineSlideFrame: true,
    });
  };
  //删除预付款行信息
  deletePrePaymentLine = (e, record) => {
    e.preventDefault();
    let url = `${config.prePaymentUrl}/api/cash/prepayment/requisitionHead/deleteLineById?lineId=${
      record.id
    }`;
    let prePayPagination = this.state.prePayPagination;
    const { page, pageSize } = this.state;
    this.setState({ planLoading: true });
    httpFetch
      .delete(url)
      .then(() => {
        message.success(`删除成功`);
        this.setState({
          prePayPagination: {
            ...prePayPagination,
            total: prePayPagination.total - 1,
            current:
              parseInt((prePayPagination.total - 2) / pageSize) < page
                ? parseInt((prePayPagination.total - 2) / pageSize) + 1
                : page + 1,
          },
          page:
            parseInt((prePayPagination.total - 2) / pageSize) < page
              ? parseInt((prePayPagination.total - 2) / pageSize)
              : page,
        });
        this.getPrepayLines();
        // this.getAmountByHeadId();
      })
      .catch(e => {
        this.setState({ planLoading: false });
        message.error(`删除失败，${e.response && e.response.data.message}`);
      });
  };
  //打开 预付款单行侧滑
  showPrePaymentLineSlide = flag => {
    this.setState({ showPrePaymentLineSlideFrame: flag, flag: flag });
  };
  //关闭 预付款单行侧滑
  handleClosePrePaymentLineSlide = params => {
    this.setState(
      {
        showPrePaymentLineSlideFrame: false,
        flag: false,
      },
      () => {
        if (params) {
          this.getList();
          // this.getInfo();
          // this.getAmountByHeadId();
        }
      }
    );
  };

  render() {
    const {
      lineInfo,
      showLineDetail,
      columns,
      record,
      lineLoading,
      pagination,
      slideFrameTitle,
      showSlideFrame,
      headerInfo,
      backLoadding,
      showContract,

      prePaymentTypeMenu,
      prePaymentColumns,
      data,
      planLoading,
      initiatePrepayment,
      paymentInformation,
      prePaymentHead,
      showPrePaymentLineSlideFrame,
      prePayPagination,
    } = this.state;
    const { headerData } = this.props;

    const menusUi = (
      <Menu onClick={this.insertPrePaymentHead}>
        {prePaymentTypeMenu.map(item => {
          return <Menu.Item key={item.id}>{item.typeName}</Menu.Item>;
        })}
      </Menu>
    );

    /**根据单据状态确定该显示什么按钮 */
    let status = null;
    if (headerData.status === 1001 || headerData.status === 1003 || headerData.status === 1005) {
      status = (
        <h3 className="header-title" style={{ textAlign: 'right', marginBottom: '10px' }}>
          <Button type="primary" onClick={this.edit}>
            {this.$t('expense.knitting.series')}
            {/*编 辑*/}
          </Button>
        </h3>
      );
    } else if (headerData.status === 1002 && this.props.flag) {
      status = (
        <h3 className="header-title" style={{ textAlign: 'right', marginBottom: '10px' }}>
          <Button loading={backLoadding} type="primary" onClick={this.back}>
            {this.$t('expense.taken.back.to.the')}
            {/*撤 回*/}
          </Button>
        </h3>
      );
    } else {
      status = <h3 className="header-title" />;
    }

    return (
      <div className="pre-payment-common">
        <Card
          style={{
            boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)',
          }}
          bodyStyle={{ padding: '24px 32px', paddingTop: 0 }}
        >
          <DocumentBasicInfo params={headerInfo}>{status}</DocumentBasicInfo>
        </Card>

        <Card
          style={{
            marginTop: 20,
            boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)',
          }}
          title={this.$t('expense.application.information')} /*申请信息*/
        >
          <div className="table-header">
            <div className="table-header-buttons" style={{ float: 'left' }}>
              {(headerData.status === 1001 ||
                headerData.status === 1003 ||
                headerData.status === 1005) && (
                <Button type="primary" onClick={this.addItem}>
                  {this.$t('expense.new.application.information')}
                  {/*新建申请信息*/}
                </Button>
              )}
            </div>
            {lineInfo.currencyAmount && (
              <div style={{ float: 'right' }}>
                <Breadcrumb style={{ marginBottom: '10px', lineHeight: '32px' }}>
                  <Breadcrumb.Item>
                    {this.$t('expense.apply.amount')}:/*申请金额*/
                    <span style={{ color: 'green' }}>
                      {' ' + lineInfo.currencyAmount.currencyCode}{' '}
                      {this.filterMoney(lineInfo.currencyAmount.amount)}
                    </span>
                  </Breadcrumb.Item>
                  <Breadcrumb.Item>
                    {this.$t('expense.local.currency.amount')}:<span style={{ color: 'green' }}>
                      {/*本币金额*/}
                      {' ' + this.props.company.baseCurrency}{' '}
                      {this.filterMoney(lineInfo.currencyAmount.functionalAmount)}
                    </span>
                  </Breadcrumb.Item>
                </Breadcrumb>
              </div>
            )}
          </div>
          <Table
            style={{ clear: 'both' }}
            rowKey={record => record.id}
            columns={columns}
            dataSource={lineInfo.lines || []}
            bordered
            loading={lineLoading}
            size="middle"
            pagination={pagination}
            expandedRowRender={this.expandedRow}
            onChange={this.tableChange}
            scroll={{ x: 1300 }}
          />
        </Card>

        {headerData.prePaymentFlag &&
          initiatePrepayment && (
            <Row style={{ marginBottom: 10, marginTop: 10 }}>
              <Col id="application-form-drop" style={{ position: 'relative' }} span={18}>
                <Dropdown
                  getPopupContainer={() => document.getElementById('application-form-drop')}
                  trigger={['click']}
                  overlay={menusUi}
                >
                  <Button type="primary">
                    {this.$t('initiate.prepayment')}
                    <Icon type="down" />
                    {/*发起预付款*/}
                  </Button>
                </Dropdown>
              </Col>
            </Row>
          )}
        {paymentInformation && (
          <Card
            style={{
              marginTop: 20,
              boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)',
            }}
            title={this.$t('expense.payment.information')} /*付款信息*/
          >
            <div className="table-header">
              <div className="table-header-buttons">
                {(headerData.status === 1001 ||
                  headerData.status === 1003 ||
                  headerData.status === 1005) && (
                  <Button type="primary" onClick={this.addPrePaymentLine}>
                    {this.$t('expense.new.payment.information')}
                    {/*新建付款信息*/}
                  </Button>
                )}
                {/*<Popconfirm title="确认删除吗？" onConfirm={e => this.deletePrePaymentLine(e, record)}>
                <a>删除</a>
              </Popconfirm>*/}
                <div style={{ float: 'right', lineHeight: '32px' }}>
                  预付款单号:{prePaymentHead.requisitionNumber}
                </div>
              </div>
              {/*{amountText !== '' ? (
              <div style={{ float: 'right' }}>
                <Breadcrumb style={{ marginBottom: '10px' }}>
                  <Breadcrumb.Item>
                    金额:<span style={{ color: 'Green' }}>{amountText}</span>
                  </Breadcrumb.Item>
                  <Breadcrumb.Item>
                    本币金额:<span style={{ color: 'Green' }}>
                        {' '}
                    {this.props.company.baseCurrency} {this.filterMoney(functionAmount)}
                      </span>
                  </Breadcrumb.Item>
                </Breadcrumb>
              </div>
            ) : null}*/}
            </div>
            <Table
              style={{ clear: 'both' }}
              rowKey={record => record.id}
              columns={prePaymentColumns}
              dataSource={data}
              bordered
              loading={planLoading}
              size="middle"
              pagination={prePayPagination}
              expandedRowRender={this.expandedRow}
            />
          </Card>
        )}

        <div
          style={{
            marginTop: 20,
            boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)',
          }}
        >
          <ApproveHistory type="801009" oid={headerData.documentOid} />
        </div>

        <SlideFrame
          title={slideFrameTitle}
          show={showSlideFrame}
          onClose={() => this.showSlide(false)}
        >
          <NewApplicationLine
            close={this.handleCloseSlide}
            headerData={this.props.headerData}
            lineId={record.id}
          />
        </SlideFrame>

        <SlideFrame
          title={this.$t('expense.details')} /*详情*/
          show={showLineDetail}
          onClose={() => {
            this.setState({ showLineDetail: false });
          }}
        >
          <ApplicationLineDetail
            close={() => {
              this.setState({ showLineDetail: false });
            }}
            headerData={this.props.headerData}
            lineId={record.id}
          />
        </SlideFrame>

        {/*预付款单行侧滑框*/}
        <SlideFrame
          title={slideFrameTitle}
          show={showPrePaymentLineSlideFrame}
          onClose={() => this.showPrePaymentLineSlide(false)}
        >
          <NewPrePaymentDetail
            onClose={this.handleClosePrePaymentLineSlide}
            params={{
              id: prePaymentHead.id,
              flag: this.state.flag,
              record,
              headerData: prePaymentHead,
              companyId: headerData.companyId,
              paymentReqTypeId: prePaymentHead.paymentReqTypeId,
              remark: prePaymentHead.description,
            }}
          />
        </SlideFrame>

        <Modal
          title={this.$t('expense.the.contract.details')} /*合同详情*/
          visible={showContract}
          onCancel={() => {
            this.setState({ showContract: false });
          }}
          width="90%"
          bodyStyle={{
            maxHeight: '70vh',
            overflow: 'auto',
            padding: '0 10px',
          }}
          footer={null}
        >
          <ContractDetail id={headerData.contractHeaderId} isApprovePage={true} />
        </Modal>
      </div>
    );
  }
}

PrePaymentCommon.propTypes = {
  id: PropTypes.any.isRequired, //显示数据
  flag: PropTypes.bool, //是否显示审批历史
};

PrePaymentCommon.defaultProps = {
  flag: true,
};

const wrappedPrePaymentCommon = Form.create()(PrePaymentCommon);
function mapStateToProps(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
  };
}
export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(wrappedPrePaymentCommon);

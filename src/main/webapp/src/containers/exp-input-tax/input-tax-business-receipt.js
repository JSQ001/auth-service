import React, { Component } from 'react';
import DocumentBasicInfo from 'widget/Template/document-basic-info';
import ApproveHistory from 'widget/Template/approve-history-work-flow';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { Card, Spin, Affix, Row, Button, Popover, Divider, message, Tabs } from 'antd';
import Table from 'widget/table';
import SlideFrame from 'widget/slide-frame';
import HeaderEdit from './header-edit';
import EditLineFrame from './edit-line-frame';
import service from './service';

class BusinessReceipt extends Component {
  constructor(props) {
    super(props);
    this.state = {
      headerInfo: {}, // 部分头信息，用于头组件内
      docHeadData: {}, // 头信息
      operationLoading: false, // btn loading
      totalAmount: 0, // 总金额
      totalTax: 0, // 总税额
      columns: [
        {
          title: this.$t('common.sequence'), // '序号',
          dataIndex: 'num',
          align: 'center',
          width: 60,
          render: (text, record) => {
            return <span>{record.key}</span>;
          },
        },
        {
          title: this.$t('acp.requisitionNumber'), // '单据编号',
          dataIndex: 'documentNumber',
          align: 'center',
          width: 150,
          render: (value, record) => {
            return (
              <Popover content={value}>
                <a onClick={() => this.showDocumentDetail(record)}>{value}</a>
              </Popover>
            );
          },
        },
        {
          title: this.$t('common.expense.type'), // '费用类型',
          dataIndex: 'expenseTypeName',
          align: 'center',
          width: 150,
          render: value => <Popover content={value}>{value}</Popover>,
        },
        {
          title: this.$t('tax.use.type'), // '用途类型',
          dataIndex: 'useTypeName',
          align: 'center',
          width: 150,
          render: value => <Popover content={value}>{value}</Popover>,
        },
        {
          title:
            props.match.params.type === 'FOR_SALE'
              ? this.$t('tax.sale.amount')
              : this.$t('tax.turnOut.tax'), // '视同销售金额' : '转出税额',
          dataIndex: 'baseAmount',
          align: 'center',
          width: 120,
          render: (amount, record) => (
            <span>
              <Popover
                content={
                  <span>
                    {`${record.currencyCode} `}
                    {this.filterMoney(amount, 2, true)}
                  </span>
                }
              >
                {`${record.currencyCode} `}
                {this.filterMoney(amount, 2, true)}
              </Popover>
            </span>
          ),
        },
        {
          title:
            props.match.params.type === 'FOR_SALE'
              ? this.$t('tax.rate')
              : this.$t('tax.turn.out.proportion'), // '税率':'转出比例',
          dataIndex: 'transferProportion',
          align: 'center',
          width: 120,
          render: value => <Popover content={value}>{value}</Popover>,
        },
        {
          title:
            props.match.params.type === 'FOR_SALE'
              ? this.$t('tax.as.sale.tax')
              : this.$t('tax.transfer.amount'), // '视同销售税额' : '转出金额',
          dataIndex: 'amount',
          align: 'center',
          width: 120,
          render: (amount, record) => (
            <span>
              <Popover
                content={
                  <span>
                    {`${record.currencyCode} `}
                    {this.filterMoney(amount, 2, true)}
                  </span>
                }
              >
                {`${record.currencyCode} `}
                {this.filterMoney(amount, 2, true)}
              </Popover>
            </span>
          ),
        },
        {
          title: this.$t('common.remark'),
          dataIndex: 'description',
          align: 'center',
          width: 150,
          render: text => <Popover content={text}>{text}</Popover>,
        },
        {
          title: this.$t('tax.check.info'),
          dataIndex: 'checkInfo',
          align: 'center',
          width: 150,
          fixed: 'right',
          render: (text, record) => {
            return (
              <a
                onClick={() => {
                  this.handleCheckInfo(record);
                }}
              >
                {/* 原费用信息 */}
                {this.$t('tax.origin.expense.info')}
              </a>
            );
          },
        },
        {
          title: this.$t('common.operation'),
          dataIndex: 'operation',
          align: 'center',
          width: 150,
          fixed: 'right',
          render: (text, record) => {
            return (
              <div>
                <a
                  onClick={() => {
                    this.handleLineEdit(record);
                  }}
                >
                  {/* 编辑 */}
                  {this.$t('common.edit')}
                </a>
                <Divider type="vertical" />
                <a
                  onClick={() => {
                    this.handleLineDel(record);
                  }}
                >
                  {this.$t('common.delete')}
                </a>
              </div>
            );
          },
        },
      ],
      dataSources: [], // 行数据
      page: 0,
      size: 10,
      pagination: {
        showSizeChanger: true,
        showQuickJumper: true,
        showTotal: (total, range) =>
          this.$t('common.show.total', {
            range0: `${range[0]}`,
            range1: `${range[1]}`,
            total,
          }),
        current: 1,
        pageSize: 5,
        pageSizeOptions: ['5', '10', '20', '50', '100'],
      },
      loading: false,
      editFrameVisible: false,
      model: {}, // 行编辑数据record
      headerView: false,
    };
  }

  componentWillMount = () => {
    this.getHeaderList();
  };

  componentWillReceiveProps(nextProps) {}

  componentDidMount = () => {
    this.getLineList();
  };

  // 转换业务大类type
  transferTypeValue = (type, rate) => {
    let newType = '';
    switch (type) {
      case 'FOR_SALE':
        // newType = `视同销售-${rate}%`;
        newType = `${this.$t('tax.as.sale')}-${rate}%`;
        break;
      case 'ALL_TRANSFER':
        // newType = '全额转出';
        newType = `${this.$t('tax.full.roll.out')}`;
        break;
      case 'PART_TRANSFER':
        // newType = `按比例转出-${rate}%`;
        newType = `${this.$t('tax.scale.out')}-${rate}%`;
        break;
      default:
        break;
    }
    return newType;
  };

  // 转换status
  transferStatus = status => {
    let newStatus = '';
    switch (status) {
      case '1002':
        newStatus = 3002;
        break;
      case '1004':
        newStatus = 1006;
        break;
      case '1005':
        newStatus = 1007;
        break;
      default:
        newStatus = Number(status);
        break;
    }
    return newStatus;
  };

  // 获取头信息
  getHeaderList = () => {
    const { match } = this.props;
    service
      .getBusinessReceiptHeadValue(match.params.id)
      .then(res => {
        const columns = this.reSetColumns(res.data.status);
        const newType = this.transferTypeValue(
          String(res.data.transferType),
          res.data.transferProportion
        );
        const newStatus = this.transferStatus(String(res.data.status));
        const headerInfo = {
          createdDate: res.data.createdDate,
          formName: this.$t('tax.business.receipt.input.tax'), // '进项税业务单'
          currencyCode: res.data.currencyCode,
          statusCode: newStatus,
          attachments: res.data.attachments,
          businessCode: res.data.documentNumber,
          createByName: res.data.fullName,
          infoList: [
            { label: this.$t('common.applicant'), value: res.data.fullName },
            { label: this.$t('acp.company'), value: res.data.companyName },
            { label: this.$t('common.department'), value: res.data.departmentName },
            { label: this.$t('tax.business.categories'), value: newType },
            { label: this.$t('tax.use.type'), value: res.data.useTypeName },
          ],
          remark: res.data.description,
          totalAmount: res.data.amount,
        };
        this.setState({
          columns,
          headerInfo,
          docHeadData: { ...res.data },
        });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  // 获取行信息
  getLineList = () => {
    debugger;
    const {
      pagination: { pageSize, current },
      pagination,
    } = this.state;
    let headerId = this.props.match.params.id;
    service
      .getBusinessReceiptList({ headerId: headerId, size: pageSize, page: current - 1 })
      .then(res => {
        debugger;
        this.setState({ dataSources: res.data, pageLoading: false });
      })
      .catch(err => {
        message.error(err.response.data.message);
        this.setState({ pageLoading: false });
      });
    // this.setState({
    //   dataSources: [
    //     {
    //       key: 1,
    //       keyIndex: 0,
    //       documentNumber: '123234',
    //       baseAmount: 1000,
    //       currencyCode: 'CNY',
    //       amount: 200,
    //     },
    //   ],
    // });
    // const { page, size, pagination } = this.state;
    // let { totalAmount, totalTax } = this.state;
    // const { match } = this.props;
    // this.setState({ loading: true });
    // service
    //   .getBusinessReceiptList({ page, size, headerId: match.params.id })
    //   .then(res => {
    //     console.log(res.data);
    //     pagination.total = Number(res.headers['x-total-count']);
    //     // 设置序号
    //     const data = res.data.map((item,index) => {
    //       const tempItem = { ...item };
    //       tempItem.key = (index + page * size) + 1;
    //       tempItem.keyIndex = index;
    //       totalAmount += tempItem.baseAmount;
    //       totalTax += tempItem.amount.
    //       return tempItem;
    //     });
    //     if(res.data.length < 1) return;

    //     this.setState({
    //       dataSources: data,
    //       pagination,
    //       loading: false,
    //       totalAmount,
    //       totalTax,
    //     });
    //   })
    //   .catch(err => {
    //     message.error(err.response.data.message);
    //   });
  };

  // 当为审核状态时，改变columns
  reSetColumns = status => {
    const { columns } = this.state;
    const { match } = this.props;
    if (String(status) === '1002') {
      // 审核中的columns与非审核中状态的columns不一
      const newColumn = [
        {
          title: this.$t('tax.share.row'),
          dataIndex: 'splitLine',
          align: 'center',
          width: 150,
          fixed: 'right',
          render: (text, record) => {
            return (
              <a onClick={() => this.splitLineInfo(record)}>
                {match.params.type === 'FOR_SALE'
                  ? this.$t('tax.share.row.as.sale')
                  : this.$t('tax.share.row.turnOut')
                //  '分摊行视同销售' : '分摊行转出信息'
                }
              </a>
            );
          },
        },
        {
          title: this.$t('tax.check.info'),
          dataIndex: 'checkInfo',
          align: 'center',
          width: 150,
          fixed: 'right',
          render: (text, record) => {
            return (
              <a
                onClick={() => {
                  this.handleCheckInfo(record);
                }}
              >
                {/* 原费用行信息 */}
                {this.$t('tax.origin.expense.row.info')}
              </a>
            );
          },
        },
      ];
      const tempArr = columns.splice(0, 8).concat(newColumn);
      return tempArr;
    } else return columns;
  };

  // 行编辑
  handleLineEdit = record => {
    this.setState({
      model: JSON.parse(JSON.stringify(record)),
      editFrameVisible: true,
    });
  };

  // 行删除
  handleLineDel = record => {
    service
      .deleteLineValue(record.id)
      .then(res => {
        if (res) message.success(this.$t('common.delete.success'));
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  // 原费用信息
  handleCheckInfo = record => {
    console.log(record);
  };

  // 分摊行视同销售/转出
  splitLineInfo = record => {
    console.log(record);
  };

  // 关闭侧换框
  closeSlideForm = flag => {
    this.setState(
      {
        model: {},
        editFrameVisible: false,
      },
      () => {
        if (flag) {
          this.getLineList();
        }
      }
    );
  };

  // 添加费用 btn
  addCost = () => {
    const { dispatch, match } = this.props;
    const { headerInfo } = this.state;
    dispatch(
      routerRedux.push({
        pathname: `/exp-input-tax/exp-input-tax/new-exp-input-tax/${match.params.id}/${
          match.params.type
        }/${headerInfo.currencyCode}`,
      })
    );
  };

  // 保存btn
  onSave = () => {
    const { match } = this.props;
    this.setState({ operationLoading: true });
    service
      .submitList(match.params.id)
      .then(res => {
        if (res) message.success(this.$t('pay.backlash.submitSuccess'));
        this.setState({ operationLoading: false });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  // 删除btn
  onDelete = () => {
    const { match, dispatch } = this.props;
    this.setState({ operationLoading: true });
    service
      .deleteInputTax(match.params.id)
      .then(res => {
        if (res) {
          message.success(this.$t('common.delete.success'));
          this.setState({ operationLoading: false });
          dispatch(
            routerRedux.push({
              pathname: `/exp-input-tax/exp-input-tax/exp-input-tax`,
            })
          );
        }
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  // 返回btn
  onBack = () => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: '/exp-input-tax/exp-input-tax/exp-input-tax',
      })
    );
  };

  // 分页
  tablePageChange = pagination => {
    const { page, size } = this.state;
    this.setState(
      {
        page: pagination.current - 1 || page,
        size: pagination.pageSize || size,
      },
      () => {
        this.getLineList();
      }
    );
  };

  // 单据详情
  showDocumentDetail = () => {};

  // 头编辑
  handelHeaderEdit = () => {
    this.setState({ headerView: true });
  };

  //  关闭编辑框
  closeHeaderEdit = flag => {
    this.setState({ headerView: false }, () => {
      if (flag) {
        this.getHeaderList();
      }
    });
  };

  render() {
    const {
      headerInfo,
      docHeadData,
      operationLoading,
      totalAmount,
      totalTax,
      columns,
      dataSources,
      pagination,
      loading,
      editFrameVisible,
      model,
      headerView,
    } = this.state;
    const { match } = this.props;
    let status = null;
    if (Number(docHeadData.status) === 1001 || Number(docHeadData.status) === 1005) {
      status = (
        <h3 className="header-title">
          <Button
            type="primary"
            style={{ marginBottom: '10px', float: 'right' }}
            onClick={this.handelHeaderEdit}
          >
            {this.$t('common.edit')}
          </Button>
        </h3>
      );
    } else status = <div />;
    return (
      <div>
        <Spin spinning={false}>
          <Card style={{ boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)' }}>
            <Tabs defaultActiveKey="1">
              <Tabs.TabPane key="1" tab={this.$t('acp.document.info')} style={{ border: 'none' }}>
                <DocumentBasicInfo params={headerInfo}>{status}</DocumentBasicInfo>
              </Tabs.TabPane>
            </Tabs>
          </Card>
          <Card
            style={{ marginTop: 20, boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)' }}
            title={
              match.params.type === 'FOR_SALE'
                ? this.$t('tax.as.sale.info')
                : this.$t('tax.turnOut.info')
            }
          >
            <div style={{ marginBottom: '10px' }}>
              <div style={{ float: 'left' }}>
                {Number(docHeadData.status) === 1001 || Number(docHeadData.status) === 1005 ? (
                  <Button type="primary" onClick={this.addCost}>
                    {/* 添加费用 */}
                    {this.$t('tax.expense.add')}
                  </Button>
                ) : (
                  <span />
                )}
              </div>
              <div style={{ float: 'right' }}>
                {match.params.type === 'FOR_SALE'
                  ? `${this.$t('tax.sale.amount')}: ${headerInfo.currencyCode} `
                  : `${this.$t('tax.transfer.amount')}: ${headerInfo.currencyCode} `}
                <span style={{ color: '#33994F', padding: '0 5px' }}>
                  {this.filterMoney(totalAmount, 2)}
                </span>
                {match.params.type === 'FOR_SALE'
                  ? `${this.$t('tax.as.sale.tax')}: ${headerInfo.currencyCode} `
                  : `${this.$t('tax.turnOut.tax')}: ${headerInfo.currencyCode} `}
                <span style={{ color: '#33994F', padding: '0 5px' }}>
                  {this.filterMoney(totalTax, 2)}
                </span>
              </div>
              <div style={{ clear: 'both' }} />
            </div>
            <Table
              columns={columns}
              dataSource={dataSources}
              pagination={pagination}
              loading={loading}
              onChange={this.tablePageChange}
              scroll={{ x: 1000 }}
            />
          </Card>
        </Spin>
        <div style={{ margin: '20px 0 50px', boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)' }}>
          <ApproveHistory
            type="9090"
            oid={docHeadData.documentOid || ''}
            headerId={docHeadData.id}
          />
        </div>
        <Affix
          offsetBottom={0}
          className="bottom-bar bottom-bar-approve"
          style={{
            position: 'fixed',
            bottom: 0,
            width: '100%',
            height: '50px',
            boxShadow: '0px -5px 5px rgba(0, 0, 0, 0.15)',
            background: '#fff',
            lineHeight: '50px',
            zIndex: 1,
          }}
        >
          {Number(docHeadData.status) === 1001 || Number(docHeadData.status) === 1005 ? (
            <Row style={{ marginLeft: '30px' }}>
              <Button
                style={{ marginLeft: '20px' }}
                loading={operationLoading}
                onClick={this.onSave}
                type="primary"
              >
                {/* 提交 */}
                {this.$t('common.submit')}
              </Button>
              <Button
                style={{ marginLeft: '20px' }}
                loading={operationLoading}
                onClick={this.onDelete}
              >
                {this.$t('common.delete')}
              </Button>
              <Button style={{ marginLeft: '20px' }} onClick={this.onBack}>
                {this.$t('budgetJournal.return')}
              </Button>
            </Row>
          ) : (
            <Row style={{ marginLeft: '30px' }}>
              <Button onClick={this.onBack}>{this.$t('budgetJournal.return')}</Button>
            </Row>
          )}
        </Affix>
        <SlideFrame
          title={
            match.params.type === 'FOR_SALE'
              ? this.$t('tax.as.sale.info.edit')
              : this.$t('tax.turnOut.info.edit')
          } // "编辑视同销售信息"
          show={editFrameVisible}
          onClose={() => {
            this.closeSlideForm(false);
          }}
        >
          <EditLineFrame params={model} type={match.params.type} onClose={this.closeSlideForm} />
        </SlideFrame>

        <SlideFrame
          title="编辑"
          show={headerView}
          onClose={() => {
            this.setState({ headerView: false });
          }}
        >
          <HeaderEdit
            params={docHeadData}
            getHeaderList={this.getHeaderList}
            onClose={this.closeHeaderEdit}
          />
        </SlideFrame>
      </div>
    );
  }
}

export default connect()(BusinessReceipt);

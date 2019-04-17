import React, { Component } from 'react';
import { connect } from 'dva';
import Pagination, { Button, Affix, message, Row, Tabs, Popover, Spin, Card } from 'antd';
import Table from 'widget/table';
const TabPane = Tabs.TabPane;
import accountingQueryService from 'containers/financial-view/gl-work-order/gl-work-order.service';
import DocumentBasicInfo from 'widget/Template/document-basic-info';
import 'styles/gl-work-order/my-gl-work-order/my-gl-work-order-detail.scss';
import ApproveHistory from 'widget/Template/approve-history-work-flow';
import Chooser from 'widget/chooser';
import { routerRedux } from 'dva/router';

class GLWorkOrderDetail extends Component {
  /**
   * 构造函数
   */
  constructor(props) {
    super(props);
    this.state = {
      //传给单据头组件的数据
      headerInfo: {},
      //单据头信息
      docHeadData: {},
      //维度信息
      dimensionData: [],
      /**
       * 审批历史
       */
      historyLoading: true,
      approveHistory: [],

      /**
       * 单据操作
       */
      operationLoading: false,

      //表格宽度
      tableWidth: 1050,

      /**
       * 单据行
       * lineStatus有三种状态：
       * 普通：normal
       */
      columns: [
        {
          title: this.$t('account.serial.number') /*序号*/,
          dataIndex: 'seq',
          width: 60,
          render: (seq, record, index) => {
            return <span>{index + 1}</span>;
          },
        },
        {
          title: (
            <span>
              <span style={{ color: 'red' }}>*</span>&nbsp;{this.$t('accounting.note')}
              {/*备注*/}
            </span>
          ),
          dataIndex: 'description',
          width: 150,
          render: (description, record, index) => {
            if (record.lineStatus === 'normal') {
              return (
                <Popover content={description}>
                  <span>{description}</span>
                </Popover>
              );
            }
          },
        },
        {
          title: (
            <span>
              <span style={{ color: 'red' }}>*</span>&nbsp;{this.$t('account.company')}
              {/*公司*/}
            </span>
          ),
          dataIndex: 'companyName',
          width: 180,
          tooltips: true,
          render: (companyName, record, index) => {
            if (record.lineStatus === 'normal') {
              return (
                <Popover content={companyName}>
                  <span>{companyName}</span>
                </Popover>
              );
            }
          },
        },
        {
          title: this.$t('account.responsibility.center') /*责任中心*/,
          dataIndex: 'responsibilityCenterName',
          width: 160,
          tooltips: true,
          render: (value, record, index) => {
            if (record.lineStatus === 'normal') {
              return (
                <Popover content={value}>
                  <span>{value}</span>
                </Popover>
              );
            }
          },
        },
        {
          title: (
            <span>
              <span style={{ color: 'red' }}>*</span>&nbsp;{this.$t('accounting.view.accountCode')}
              {/*科目*/}
            </span>
          ),
          dataIndex: 'accountName',
          width: 150,
          tooltips: true,
          render: (accountName, record, index) => {
            if (record.lineStatus === 'normal') {
              return (
                <Popover content={accountName}>
                  <span>{accountName}</span>
                </Popover>
              );
            }
          },
        },
        {
          title: (
            <span>
              <span style={{ color: 'red' }}>*</span>&nbsp;{this.$t('accounting.amount.debit')}
              {/*借方金额*/}
            </span>
          ),
          dataIndex: 'enteredAmountCr',
          width: 140,
          fixed: 'right',
          render: (enteredAmountDr, record, index) => {
            if (record.lineStatus === 'normal') {
              return <span>{this.filterMoney(enteredAmountDr, 2)}</span>;
            }
          },
        },
        {
          title: (
            <span>
              <span style={{ color: 'red' }}>*</span>&nbsp;{this.$t('accounting.amount.credit')}
              {/*贷方金额*/}
            </span>
          ),
          dataIndex: 'enteredAmountDr',
          width: 140,
          fixed: 'right',
          render: (enteredAmountCr, record, index) => {
            if (record.lineStatus === 'normal') {
              return <span>{this.filterMoney(enteredAmountCr, 2)}</span>;
            }
          },
        },
      ],
      loading: true,
      pagination: {
        total: 0,
        showSizeChanger: true,
        showQuickJumper: true,
      },
      data: [],
      page: 0,
      pageSize: 10,
    };
  }

  /**
   * 维值变化事件
   */
  onDimensionChange = (value, record, index, dimensionKey, dimensionName) => {
    let { data } = this.state;
    data[index][dimensionKey] = value[0].id;
    data[index][dimensionName] = value[0].dimensionItemName;
    this.setState({ data });
  };

  /**
   * 生命周期函数
   */
  componentWillMount = () => {
    this.getDocInfoById();
    this.getHistory();
  };
  /**
   * 获取审批历史
   */
  getHistory = () => {
    let documentOid = this.props.match.params.oid;
    accountingQueryService
      .getHistory(documentOid)
      .then(res => {
        if (res.status === 200) {
          this.setState({
            historyLoading: false,
            approveHistory: res.data,
          });
        }
      })
      .catch(e => {
        if (e.response) {
          message.error(`${e.response.data.message}`);
        }
        this.setState({ historyLoading: false });
      });
  };
  /**
   * 根据头id获取单据数据-初始化的时候
   */
  getDocInfoById = () => {
    let headId = this.props.match.params.id;
    let page = this.state.page;
    let size = this.state.pageSize;
    accountingQueryService
      .getHeaderData(headId, page, size)
      .then(res => {
        if (res.status === 200) {
          let docHeadData = res.data.head;
          let data = res.data.line;
          //获取到的行数据，全部都给一个index序号，用来作为唯一标识符
          data.map((item, index) => {
            data[index].key = index;
          });
          let dimensionData = res.data.dimensions;
          let headerInfo = {
            businessCode: docHeadData.workOrderNumber,
            createdDate: docHeadData.requisitionDate,
            formName: docHeadData.typeName,
            createByName: docHeadData.createByName,
            currencyCode: docHeadData.currency,
            totalAmount: docHeadData.amount,
            statusCode: docHeadData.status,
            remark: docHeadData.remark,
            infoList: [
              {
                label: this.$t('accounting.the.applicant'),
                value: docHeadData.employeeName,
              } /*申请人*/,
              { label: this.$t('accounting.the.company'), value: docHeadData.companyName } /*公司*/,
              { label: this.$t('accounting.department'), value: docHeadData.unitName } /*部门*/,
            ],
            attachments: docHeadData.attachments,
          };
          this.setState(
            {
              docHeadData,
              headerInfo,
              dimensionData,
              data,
              loading: false,
              pagination: {
                total: Number(res.headers['x-total-count'])
                  ? Number(res.headers['x-total-count'])
                  : 0,
                current: page + 1,
                onChange: this.onChangeCheckedPage,
                onShowSizeChange: this.onShowSizeChange,
                showSizeChanger: true,
                showQuickJumper: true,
                //showTotal: total => `共搜到 ${total} 条数据```,/*共搜到 条数据*/
                showTotal: (total, range) =>
                  this.$t('common.show.total', {
                    range0: `${range[0]}`,
                    range1: `${range[1]}`,
                    total: total,
                  }),
              },
            },
            () => {
              this.addDimensionColumns(dimensionData);
            }
          );
        }
      })
      .catch(e => {
        if (e.response) {
          message.error(`${e.response.data.message}`);
        }
        this.setState({
          loading: false,
        });
      });
  };
  /**
   * 切换分页
   */
  onChangeCheckedPage = page => {
    if (page - 1 !== this.state.page) {
      let { pagination, pageSize } = this.state;
      pagination.pageSize = pageSize;
      this.setState(
        {
          loading: true,
          page: page - 1,
          pagination,
        },
        () => {
          this.getDocInfoById();
        }
      );
    }
  };
  /**
   * 切换每页显示的条数
   */
  onShowSizeChange = (current, pageSize) => {
    let { pagination } = this.state;
    pagination.pageSize = pageSize;
    this.setState(
      {
        loading: true,
        page: current - 1,
        pageSize,
        pagination,
      },
      () => {
        this.getDocInfoById();
      }
    );
  };

  /**
   * 实现动态添加维度列
   */
  addDimensionColumns = dimensionData => {
    let { columns, tableWidth } = this.state;
    if (columns.length <= 8) {
      dimensionData.map(item => {
        //根据维度个数调整列宽
        tableWidth += 140;
        //维度id
        let dimensionId = item.id;
        //维度name
        let dimensionTitle = item.name;
        //维值id-字段名称
        let dimensionKey = 'dimensionValue' + item.priority + 'Id';
        //维值name-字段名称
        let dimensionName = 'dimensionValue' + item.priority + 'Name';
        //拼接列
        let dimensionColumn = {
          title: dimensionTitle,
          dataIndex: dimensionKey,
          width: 140,
          render: (text, record, index) => {
            if (record.lineStatus === 'normal') {
              return <span>{record[dimensionName]}</span>;
            } else {
              return (
                <Chooser
                  onChange={value =>
                    this.onDimensionChange(value, record, index, dimensionKey, dimensionName)
                  }
                  value={
                    record[dimensionKey]
                      ? [{ id: record[dimensionKey], dimensionItemName: record[dimensionName] }]
                      : []
                  }
                  type="dimension_value"
                  labelKey="dimensionItemName"
                  valueKey="id"
                  single={true}
                  listExtraParams={{ dimensionId: dimensionId }}
                  showClear={false}
                />
              );
            }
          },
        };
        columns.splice(5, 0, dimensionColumn);
        this.setState({
          columns,
          tableWidth,
        });
      });
    }
  };

  /**
   * 返回首页
   */
  onBack = () => {
    this.props.dispatch(
      routerRedux.push({
        pathname: `/financial-view/gl-work-order/gl-work-order`,
      })
    );
  };

  /**
   * 渲染函数
   */
  render() {
    //传给头组件的data
    const { headerInfo } = this.state;
    //头行数据
    const { docHeadData } = this.state;
    //审批历史
    const { approveHistory, historyLoading } = this.state;
    //表格
    let { columns, loading, pagination, data, tableWidth } = this.state;
    //操作
    const { operationLoading } = this.state;

    //真正渲染出来的内容
    return (
      <div
        style={{
          background: 'white',
          boxShadow: 'rgba(0, 0, 0, 0.15) 0px 2px 8px',
          padding: '0px 15px 85px 15px',
        }}
      >
        <Spin spinning={false}>
          <Card style={{ boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)' }}>
            <Tabs defaultActiveKey="1" onChange={this.tabChange} forceRender>
              <TabPane
                tab={this.$t('accounting.document.information')}
                key="1"
                style={{ border: 'none' }}
              >
                {/*单据信息*/}
                <DocumentBasicInfo params={headerInfo}>{status}</DocumentBasicInfo>
              </TabPane>
            </Tabs>
          </Card>
          <Card
            style={{ marginTop: 20, boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)' }}
            title={this.$t('accounting.accounting.information')} /*核算信息*/
          >
            <div className="table-header" style={{ marginTop: '-16px' }}>
              <Table
                style={{ clear: 'both' }}
                bordered
                size="middle"
                rowKey={record => record['key']}
                loading={loading}
                columns={columns}
                pagination={pagination}
                dataSource={data}
                scroll={{ x: 660 }}
              />
            </div>
          </Card>
        </Spin>
        <div style={{ marginTop: 20, marginBottom: 0, boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)' }}>
          <ApproveHistory
            type="801008"
            oid={docHeadData.documentOid || ''}
            headerId={docHeadData.id}
            formOid={docHeadData.formOid}
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
            boxShadow: '0px -5px 5px rgba(0, 0, 0, 0.067)',
            background: '#fff',
            lineHeight: '50px',
            zIndex: 1,
          }}
        >
          {
            <Row style={{ marginLeft: '30px' }}>
              <Button loading={operationLoading} onClick={this.onBack}>
                {this.$t('accounting.back')}
                {/*返 回*/}
              </Button>
            </Row>
          }
        </Affix>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    company: state.user.company,
    user: state.user.currentUser,
  };
}

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(GLWorkOrderDetail);

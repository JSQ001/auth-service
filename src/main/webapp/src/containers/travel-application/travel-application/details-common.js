import React from 'react';
import { connect } from 'dva';
import { Form, Button, Row, Col, Breadcrumb, message, Divider, Card, Popover, Modal } from 'antd';
import Table from 'widget/table';
import SlideFrame from 'widget/slide-frame';
import { routerRedux } from 'dva/router';
import DocumentBasicInfo from 'widget/Template/document-basic-info';
import moment from 'moment';
import PropTypes from 'prop-types';
import ApproveHistory from 'widget/Template/approve-history-work-flow';
import NewApplicationLine from './new-line';
import Chooser from 'widget/chooser';

import service from './service';

import 'styles/pre-payment/my-pre-payment/pre-payment-detail.scss';

class TravelCommon extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      lineLoading: false,
      historyLoading: false, // 控制审批历史记录是否loading
      columns: [
        {
          title: '序号',
          dataIndex: 'number',
          align: 'center',
          width: 90,
          render: (value, record, index) =>
            (this.state.pagination.current - 1) * this.state.pagination.pageSize + index + 1,
        },
        {
          title: '申请类型',
          dataIndex: 'expenseTypeName',
          align: 'center',
          width: 120,
          render: value => {
            return <Popover content={value}>{value}</Popover>;
          },
        },
        {
          title: '人员',
          dataIndex: 'travelPeopleStr',
          align: 'center',
          width: 120,
          render: value => {
            return <Popover content={value}>{value}</Popover>;
          },
        },
        {
          title: '公司',
          dataIndex: 'company',
          align: 'center',
          width: 120,
          render: (value, record, index) => {
            return record.status === 'edit' ? (
              <Chooser
                type="company"
                labelKey="name"
                valueKey="id"
                onChange={values => {
                  const { lineInfo } = this.state;
                  record.companyId = values[0].id;
                  record.companyName = values[0].name;
                  this.setState({ lineInfo });
                }}
                showClear={false}
                single
                listExtraParams={{ setOfBooksId: this.props.company.setOfBooksId }}
                value={[{ id: record.companyId, name: record.companyName }]}
              />
            ) : (
              <span>{record.companyName ? record.companyName : ''}</span>
            );
          },
        },
        {
          title: '部门',
          dataIndex: 'department',
          align: 'center',
          width: 120,
          render: (value, record, index) => {
            return record.status === 'edit' ? (
              <Chooser
                type="department_document"
                labelKey="path"
                valueKey="departmentId"
                onChange={values => {
                  const { lineInfo } = this.state;
                  record.unitId = values[0].departmentId;
                  record.departmentName = values[0].path;
                  this.setState({ lineInfo });
                }}
                showClear={false}
                single
                listExtraParams={{ tenantId: this.props.user.tenantId }}
                value={[{ departmentId: record.unitId, path: record.departmentName }]}
              />
            ) : (
              <span>{record.departmentName ? record.departmentName : ''}</span>
            );
          },
        },
        {
          title: this.$t('expense.responsibility.center') /*责任中心*/,
          dataIndex: 'responsibilityCenterCodeName',
          width: 120,
          render: value => {
            return <Popover content={value}>{value}</Popover>;
          },
        },
      ],
      showSlideFrame: false,
      slideFrameTitle: '',
      record: {},
      approveHistory: [],
      headerInfo: {},
      backLoadding: false,
      lineInfo: [],
      //是否可编辑（对行程时间的权限）
      routeBeforeApprove: '',
      //是否可编辑（对单据状态的权限）
      statusEditable: '',
      //是否可编辑（对单据状态的权限）
      statusPass: '',
      //是否可编辑（对人的权限）
      peopleEditable: '',
      pagination: {
        current: 1,
        showSizeChanger: true,
        pageSize: 5,
        pageSizeOptions: ['5', '10', '20', '50', '100'],
        showTotal: total => `共${total}条数据`,
      },
    };
  }

  componentDidMount() {
    // 获取行程填写状态
    this.setRouteBeforeApprove();
    // 设置基本信息
    // this.setBasicInfo();
    // 设置表格动态列
    // this.setTableColumns();
  }

  // 获取行程填写状态
  setRouteBeforeApprove = () => {
    const { headerData } = this.props;
    service
      .getTravelApplicationTypeById(headerData.documentTypeId)
      .then(res => {
        const routeBeforeApprove = res.data.route;
        this.setState({ routeBeforeApprove }, () => {
          // 设置基本信息
          this.setBasicInfo().then(res => {
            // 设置表格动态列
            this.setTableColumns();
          });
        });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  // 设置基本信息
  setBasicInfo = () => {
    return new Promise((resolve, reject) => {
      const { headerData } = this.props;
      const headerInfo = {
        businessCode: headerData.requisitionNumber,
        createdDate: headerData.requisitionDate,
        formName: headerData.typeName,
        createByName: headerData.createdName,
        currencyCode: headerData.currencyCode,
        totalAmount: headerData.totalFunctionAmount,
        statusCode: headerData.status,
        remark: headerData.description,
        infoList: [
          { label: '申请人', value: headerData.employeeName },
          { label: '公司', value: headerData.companyName },
          { label: '部门', value: headerData.departmentName },
          {
            label: '出差日期',
            value:
              moment(headerData.startDate).format('YYYY-MM-DD') +
              '~' +
              moment(headerData.endDate).format('YYYY-MM-DD'),
          },
          { label: '订票人', value: headerData.orderName },
          { label: '出行人员', value: headerData.travelPeopleDTOList.map(o => o.employeeName) },
        ],
        customList: headerData.dimensions
          ? headerData.dimensions
              .filter(o => o.headerFlag)
              .map(o => ({ label: o.name, value: o.valueName }))
          : [],
        attachments: headerData.attachments,
      };
      this.setState({ headerInfo });
      //设置可编辑状态
      this.setState(
        {
          statusEditable:
            headerData.status === 1001 || headerData.status === 1003 || headerData.status === 1005,
          statusPass: headerData.status === 1004,
          peopleEditable: headerData.createdBy === this.props.user.id,
        },
        resolve
      );
    });
  };

  // 设置表格动态列
  setTableColumns = () => {
    const { headerData } = this.props;
    const { columns, routeBeforeApprove, statusEditable, statusPass, peopleEditable } = this.state;

    service.getColumnInfo(headerData.id).then(res => {
      res.data.map(item => {
        columns.push({
          ...item,
          width: 150,
          align: 'center',
          render: value => {
            return <Popover content={value}>{value}</Popover>;
          },
        });
      });

      const option1 = {
        title: '订票人',
        dataIndex: 'bookerName',
        align: 'center',
        render: value => {
          return <Popover content={value}>{value}</Popover>;
        },
      };
      option1.width = 150;
      if (res.data.length > 4) {
        option1.width = 350;
      }
      columns.push(option1);

      const option2 = {
        title: '备注',
        dataIndex: 'description',
        align: 'center',
        render: value => {
          return <Popover content={value}>{value}</Popover>;
        },
      };
      option2.width = 150;
      if (res.data.length > 4) {
        option2.width = 350;
      }
      columns.push(option2);

      // 可编辑状态
      if (
        peopleEditable &&
        ((routeBeforeApprove && statusEditable) || (!routeBeforeApprove && statusPass))
      ) {
        columns.push({
          title: '操作',
          dataIndex: 'options',
          width: 120,
          align: 'center',
          fixed: 'right',
          render: (value, record) => {
            if (record.children) {
              return (
                <span>
                  <a onClick={() => this.editItem(record)}>编辑</a>
                  <Divider type="vertical" />
                  <a onClick={() => this.deleteLine(record)}>删除</a>
                </span>
              );
            } else {
              return record.status === 'edit' ? (
                <span>
                  <a onClick={() => this.handleSave(record)}>保存</a>
                  <Divider type="vertical" />
                  <a onClick={() => this.handleCancel(record)}>取消</a>
                </span>
              ) : (
                <span>
                  <a onClick={() => this.handleEdit(record)}>编辑</a>
                </span>
              );
            }
          },
        });
      } else {
        columns.push({
          title: '操作',
          dataIndex: 'options',
          width: 120,
          align: 'center',
          fixed: 'right',
          render: (value, record) => {
            if (record.children) {
              return <a onClick={() => this.editItem(record)}>查看详情</a>;
            } else {
              return <a disabled>编辑</a>;
            }
          },
        });
      }

      // 如果没有审批通过
      if (!statusPass) {
        columns.push({
          title: '',
          dataIndex: 'buttons',
          width: 120,
          align: 'center',
          fixed: 'right',
          render: (value, record) =>
            record.useFlag === 'N' &&
            record.bookerId === this.props.user.id && (
              <span>
                <Button type="primary" size="small" disabled>
                  订票
                </Button>
              </span>
            ),
        });
        // 如果审批通过
      } else if (statusPass) {
        columns.push({
          title: '',
          dataIndex: 'buttons',
          width: 120,
          align: 'center',
          fixed: 'right',
          render: (value, record) => {
            if (record.useFlag === 'N' && record.bookerId === this.props.user.id) {
              return (
                <span>
                  <Button type="primary" size="small" onClick={() => this.orderTickets(record)}>
                    订票
                  </Button>
                </span>
              );
            } else if (record.useFlag !== 'N') {
              return (
                <span>
                  <Button type="primary" onClick={() => this.lineDetail(record)}>
                    行程详情
                  </Button>
                </span>
              );
            } else {
              return <span />;
            }
          },
        });
      }

      this.setState({ columns }, () => {
        this.getLineInfo();
      });
    });
  };

  // 获取行数据
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
        const { headerInfo } = this.state;
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

  // 删除行数据
  deleteLine = ({ id }) => {
    service
      .deleteLine(id)
      .then(res => {
        message.success('删除成功！');
        const { pagination } = this.state;
        pagination.current = 1;
        this.setState({ pagination }, () => {
          this.getLineInfo();
        });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  // 侧滑
  showSlide = flag => {
    this.setState({ showSlideFrame: flag, record: {} });
  };

  // 关闭侧滑
  handleCloseSlide = flag => {
    const { pagination } = this.state;
    this.setState({ showSlideFrame: false, record: {} }, () => {
      if (flag) {
        pagination.current = 1;
        this.setState({ pagination }, this.getLineInfo);
      }
    });
  };

  // 编辑
  edit = () => {
    this.props.dispatch(
      routerRedux.push({
        pathname: `/travel-application/edit-travel-application/${this.props.headerData.id}`,
      })
    );
  };

  // 取消
  onCancel = () => {
    this.props.dispatch(
      routerRedux.push({
        pathname: '/travel-application/travel-application-form',
      })
    );
  };

  // 撤销
  back = () => {
    const params = {
      entities: [
        {
          entityOid: this.props.headerData.documentOid,
          entityType: 801010,
        },
      ],
    };
    this.setState({ backLoadding: true });
    service
      .withdraw(params)
      .then(res => {
        this.setState({ backLoadding: false });
        message.success('撤回成功！');
        this.onCancel();
      })
      .catch(err => {
        this.setState({ backLoadding: false });
        message.error(err.response.data.message);
      });
  };

  // 添加行信息
  addItem = () => {
    this.setState({ showSlideFrame: true, slideFrameTitle: '新建行程信息' });
  };

  // 编辑行信息
  editItem = record => {
    this.setState({ record }, () => {
      this.setState({ showSlideFrame: true, slideFrameTitle: '编辑行程信息' });
    });
  };

  //保存
  handleSave = record => {
    let params = {
      id: record.id,
      companyId: record.companyId,
      unitId: record.unitId,
    };
    service
      .updateApplicationLineDetail(params)
      .then(res => {
        message.success('修改成功！');
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
    record.status = 'normal';
    const { lineInfo } = this.state;
    this.setState({ lineInfo });
  };

  //编辑状态
  handleEdit = record => {
    record.status = 'edit';
    const { lineInfo } = this.state;
    this.setState({ lineInfo });
  };

  //取消
  handleCancel = record => {
    record.status = 'normal';
    const { lineInfo } = this.state;
    this.setState({ lineInfo });
  };

  // 查看行详情
  lineDetail = record => {
    Modal.confirm({
      title: '行详情',
      content: '假装有详情！',
      onOk: () => {},
      onCancel: () => {},
    });
  };

  //订票
  orderTickets = record => {
    Modal.confirm({
      title: '订票',
      content: '假装订票！',
      onOk: () => {},
      onCancel: () => {},
    });
  };

  tableChange = pagination => {
    this.setState({ pagination }, () => {
      this.getLineInfo();
    });
  };

  render() {
    const {
      lineInfo,
      columns,
      record,
      lineLoading,
      pagination,
      slideFrameTitle,
      showSlideFrame,
      headerInfo,
      backLoadding,
      routeBeforeApprove,
      statusEditable,
      statusPass,
      peopleEditable,
    } = this.state;
    const { headerData } = this.props;

    /** 根据单据状态确定该显示什么按钮 */
    let status = null;
    if (statusEditable && peopleEditable) {
      status = (
        <h3 className="header-title" style={{ textAlign: 'right', marginBottom: '10px' }}>
          <Button type="primary" onClick={this.edit}>
            编 辑
          </Button>
        </h3>
      );
    } else if (headerData.status === 1002 && this.props.flag && peopleEditable) {
      status = (
        <h3 className="header-title" style={{ textAlign: 'right', marginBottom: '10px' }}>
          <Button loading={backLoadding} type="primary" onClick={this.back}>
            撤 回
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
          title="行程信息"
        >
          <div className="table-header">
            <div className="table-header-buttons" style={{ float: 'left' }}>
              {peopleEditable &&
                statusEditable && (
                  <Button type="primary" onClick={this.addItem} disabled={!routeBeforeApprove}>
                    新建行程信息
                  </Button>
                )}
              {peopleEditable &&
                statusPass && (
                  <Button type="primary" onClick={this.addItem} disabled={routeBeforeApprove}>
                    新建行程信息
                  </Button>
                )}
            </div>
            {lineInfo.currencyAmount && (
              <div style={{ float: 'right' }}>
                <Breadcrumb style={{ marginBottom: '10px', lineHeight: '32px' }}>
                  <Breadcrumb.Item>
                    申请金额:
                    <span style={{ color: 'green' }}>
                      {` ${lineInfo.currencyAmount.currencyCode}`}{' '}
                      {this.filterMoney(lineInfo.currencyAmount.amount)}
                    </span>
                  </Breadcrumb.Item>
                  <Breadcrumb.Item>
                    本币金额:<span style={{ color: 'green' }}>
                      {` ${this.props.company.baseCurrency}`}{' '}
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
            dataSource={lineInfo || []}
            bordered
            loading={lineLoading}
            size="small"
            pagination={pagination}
            onChange={this.tableChange}
            scroll={{ x: 800 }}
          />
        </Card>

        <div
          style={{
            marginTop: 20,
            boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)',
          }}
        >
          <ApproveHistory type="801010" oid={headerData.documentOid} />
        </div>

        <SlideFrame
          title={slideFrameTitle}
          show={showSlideFrame}
          onClose={() => this.showSlide(false)}
        >
          <NewApplicationLine
            close={this.handleCloseSlide}
            headerData={this.props.headerData}
            statusEditable={
              peopleEditable &&
              ((routeBeforeApprove && statusEditable) || (!routeBeforeApprove && statusPass))
            }
            lineId={record.id}
          />
        </SlideFrame>
      </div>
    );
  }
}

TravelCommon.propTypes = {
  id: PropTypes.any.isRequired, // 显示数据
  flag: PropTypes.bool, // 是否显示审批历史
};

TravelCommon.defaultProps = {
  flag: true,
};

const wrappedTravelCommon = Form.create()(TravelCommon);
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
)(wrappedTravelCommon);

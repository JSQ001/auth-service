import React from 'react';
import { connect } from 'dva';
import { Form, Breadcrumb, message, Card, Popover } from 'antd';
import Table from 'widget/table';
import DocumentBasicInfo from 'widget/Template/document-basic-info';
import moment from 'moment';
import PropTypes from 'prop-types';
import ApproveHistory from 'widget/Template/approve-history-work-flow';
import NewApplicationLine from './new-line';
import SlideFrame from 'widget/slide-frame';

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
            return <span>{record.companyName ? record.companyName : ''}</span>;
          },
        },
        {
          title: '部门',
          dataIndex: 'department',
          align: 'center',
          width: 120,
          render: (value, record, index) => {
            return <span>{record.departmentName ? record.departmentName : ''}</span>;
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
    // 设置基本信息
    this.setBasicInfo();
    // 设置表格动态列
    this.setTableColumns();
  }

  // 设置基本信息
  setBasicInfo = () => {
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
  };

  // 设置表格动态列
  setTableColumns = () => {
    const { headerData } = this.props;
    const { columns } = this.state;

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

  tableChange = pagination => {
    this.setState({ pagination }, () => {
      this.getLineInfo();
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

  // 编辑行信息
  editItem = record => {
    this.setState({ record }, () => {
      this.setState({ showSlideFrame: true, slideFrameTitle: '编辑行程信息' });
    });
  };

  render() {
    const {
      lineInfo,
      columns,
      lineLoading,
      pagination,
      headerInfo,
      record,
      slideFrameTitle,
      showSlideFrame,
    } = this.state;
    const { headerData } = this.props;

    let status = <h3 className="header-title" />;

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
            statusEditable={false}
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

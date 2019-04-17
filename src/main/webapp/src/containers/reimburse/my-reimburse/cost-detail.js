import React from 'react';
import { connect } from 'dva';
import {
  Form,
  Select,
  Tag,
  Input,
  Progress,
  Tabs,
  Button,
  Menu,
  Radio,
  Dropdown,
  Row,
  Col,
  Spin,
  Timeline,
  message,
  Popover,
  Popconfirm,
  Icon,
  Divider,
  Modal,
} from 'antd';
import Table from 'widget/table';
import config from 'config';
import moment from 'moment';

import 'styles/reimburse/reimburse.scss';

import reimburseService from 'containers/reimburse/my-reimburse/reimburse.service';
import ApprotionInfo from 'containers/reimburse/my-reimburse/approtion-info';

const FormItem = Form.Item;
const Option = Select.Option;
const CheckableTag = Tag.CheckableTag;
const { TextArea } = Input;

class CostDetail extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      pagination: {
        total: 0,
      },
      page: 0,
      pageSize: 5,
      columns: [
        {
          title: '序号',
          width: 80,
          align: 'center',
          dataIndex: 'index',
          key: 'index',
          render: (value, record, index) => {
            return <span>{this.state.page * this.state.pageSize + index + 1}</span>;
          },
        },
        {
          title: '费用类型',
          width: 180,
          align: 'center',
          dataIndex: 'expenseTypeName',
          key: 'expenseTypeName',
        },
        {
          title: '发生日期',
          width: 120,
          align: 'center',
          dataIndex: 'expenseDate',
          key: 'expenseDate',
          render: value => moment(value).format('YYYY-MM-DD'),
        },
        {
          title: '金额',
          width: 100,
          align: 'center',
          dataIndex: 'amount',
          key: 'amount',
          render: desc => this.filterMoney(desc),
        },
        {
          title: '本位币金额',
          width: 120,
          align: 'center',
          dataIndex: 'functionAmount',
          key: 'functionAmount',
          render: desc => this.filterMoney(desc),
        },
        {
          title: '备注',
          dataIndex: 'description',
          align: 'center',
          key: 'description',
          render: value => {
            return <Popover content={value}>{value || '-'}</Popover>;
          },
        },
        {
          title: '查看信息',
          dataIndex: 'option',
          width: 160,
          align: 'center',
          render: (value, record) => {
            return (
              <div>
                {
                  <a
                    onClick={e => {
                      e.preventDefault();
                      e.stopPropagation();
                      this.showInvoiceDetail(record);
                    }}
                  >
                    发票信息
                  </a>
                }
                <Divider type="vertical" />
                <a
                  onClick={e => {
                    e.preventDefault();
                    e.stopPropagation();
                    this.showApprotion(record);
                  }}
                >
                  分摊信息
                </a>
              </div>
            );
          },
        },
        {
          title: '操作',
          dataIndex: 'id',
          width: 160,
          align: 'center',
          render: (value, record) => {
            return (
              <div>
                <a
                  onClick={e => {
                    e.preventDefault();
                    e.stopPropagation();
                    this.copy(record);
                  }}
                >
                  复制
                </a>
                <Divider type="vertical" />
                <a
                  onClick={e => {
                    e.preventDefault();
                    e.stopPropagation();
                    this.edit(record);
                  }}
                >
                  编辑
                </a>
                <Divider type="vertical" />
                <Popconfirm
                  placement="top"
                  title={'确认删除？'}
                  onConfirm={e => {
                    e.preventDefault();
                    this.deleteCost(record);
                  }}
                  okText="确定"
                  cancelText="取消"
                >
                  <a
                    onClick={e => {
                      e.preventDefault();
                      e.stopPropagation();
                    }}
                  >
                    删除
                  </a>
                </Popconfirm>
              </div>
            );
          },
        },
      ],
      data: [],
      expenseTypeList: [],
      loading: false,
      headerData: {},
      flag: false,
      previewVisible: false,
      previewImage: '',
      invoiceId: '',
      showApprotion: false,
    };
  }
  componentWillReceiveProps(nextProps) {
    if (this.state.flag != nextProps.flag) {
      this.setState({ flag: nextProps.flag, page: 0, headerData: nextProps.headerData }, () => {
        this.getList();
        //this.getInfo();
      });
    }

    if (nextProps.disabled && this.state.columns.length == 8) {
      let columns = this.state.columns;
      columns.splice(columns.length - 1, 1);
      this.setState({ columns });
    }

    if (nextProps.headerData.id && !this.state.headerData.id) {
      this.setState({ headerData: nextProps.headerData }, () => {
        let expenseTypeList = [];
        if (
          this.state.headerData.summaryView &&
          this.state.headerData.summaryView.expenseTypeList
        ) {
          expenseTypeList = this.state.headerData.summaryView.expenseTypeList;
        }
        this.setState({ expenseTypeList });
        this.getList();
      });
    }
  }

  //获取报账单头信息
  getInfo = () => {
    reimburseService.getReimburseDetailById(this.state.headerData.id).then(res => {
      let expenseTypeList = [];
      if (res.data.summaryView && res.data.summaryView.expenseTypeList) {
        expenseTypeList = res.data.summaryView.expenseTypeList;
      }
      this.setState({
        headerData: res.data,
        expenseTypeList,
      });
    });
  };

  //获取列表
  getList = (typeId = '') => {
    this.setState({ loading: true });
    const { page, pageSize, headerData } = this.state;
    const params = {
      page: page,
      size: pageSize,
      reportHeaderId: headerData.id,
    };
    reimburseService
      .getCostLineInfo(params)
      .then(res => {
        this.setState({
          data: res.data,
          pagination: {
            total: Number(res.headers['x-total-count']) ? Number(res.headers['x-total-count']) : 0,
            current: this.state.page + 1,
            pageSize: this.state.pageSize,
            onChange: this.onChangePaper,
            showSizeChanger: true,
            showQuickJumper: true,
            onShowSizeChange: this.onShowSizeChange,
            showTotal: (total, range) =>
              this.$t('common.show.total', {
                range0: `${range[0]}`,
                range1: `${range[1]}`,
                total: total,
              }),
            pageSizeOptions: ['5', '10'],
          },
          loading: false,
        });
      })
      .catch(err => {
        message.error('获取数据失败！');
      });
  };

  showInvoiceDetail = record => {
    this.props.showInvoiceDetail && this.props.showInvoiceDetail(record);
  };

  //显示分摊行
  showApprotion = record => {
    this.setState({ invoiceId: record.id, showApprotion: true });
  };

  //获取费用行列表
  getCostList = id => {
    this.setState({ page: 0 }, () => {
      this.getList(id);
    });
  };

  //分页
  onChangePaper = page => {
    if (page - 1 !== this.state.page) {
      this.setState({ page: page - 1 }, () => {
        this.getList();
      });
    }
  };
  onShowSizeChange = (current, pageSize) => {
    this.setState(
      {
        page: current - 1,
        pageSize: pageSize,
      },
      () => {
        this.getList();
      }
    );
  };

  //编辑
  edit = record => {
    this.props.costEdit && this.props.costEdit(record);
  };

  //详情
  detail = record => {
    if (!this.props.disabled) return;
    this.props.costDetail && this.props.costDetail(record);
  };

  //复制
  copy = record => {
    this.props.costCopy && this.props.costCopy(record);
  };

  //删除
  deleteCost = record => {
    this.props.deleteCost && this.props.deleteCost(record);
  };

  //图片预览
  preview = record => {
    this.setState({ previewVisible: true, previewImage: record.thumbnailUrl });
  };

  //表格展开的内容
  expandedRowRender = record => {
    return (
      <Row>
        <Col style={{ textAlign: 'right', width: 100 }} span={2}>
          <h3>{this.$t('my.contract.enclosure.information')}：</h3>
        </Col>
        <Col span={20}>
          <Row>
            {record.attachments &&
              record.attachments.map(item => {
                return (
                  <Col
                    span={6}
                    style={{
                      textAlign: 'left',
                      overflow: 'hidden',
                      textOverflow: 'ellipsis',
                      whiteSpace: 'nowrap',
                    }}
                    key={item.id}
                  >
                    <Popover content={item.fileName}>
                      {item.fileType !== 'IMAGE' ? (
                        <a
                          href={`${config.baseUrl}/api/attachments/download/${
                            item.attachmentOid
                          }?access_token=${sessionStorage.getItem('token')}`}
                        >
                          {item.fileName}
                        </a>
                      ) : (
                        <a
                          onClick={() => {
                            this.preview(item);
                          }}
                        >
                          {item.fileName}
                        </a>
                      )}
                    </Popover>
                  </Col>
                );
              })}
          </Row>
        </Col>
      </Row>
    );
  };

  render() {
    let {
      loading,
      data,
      columns,
      pagination,
      headerData,
      expenseTypeList,
      previewVisible,
      previewImage,
    } = this.state;
    return (
      <div>
        <div style={{ clear: 'both', padding: '10px 0' }}>
          <Table
            size="middle"
            rowKey={record => record.id}
            loading={loading}
            columns={columns}
            dataSource={data}
            pagination={pagination}
            bordered
            expandedRowRender={this.expandedRowRender}
            onRow={record => ({ onClick: () => this.detail(record) })}
          />
        </div>
        <Modal
          visible={previewVisible}
          footer={null}
          onCancel={() => {
            this.setState({ previewVisible: false });
          }}
        >
          <img alt="picture is missing." style={{ width: '100%' }} src={previewImage} />
        </Modal>
        <ApprotionInfo
          close={() => {
            this.setState({ showApprotion: false });
          }}
          params={{
            expenseReportTypeId: headerData.documentTypeId,
            headerId: headerData.id,
          }}
          headerData={this.state.headerData}
          visible={this.state.showApprotion}
          id={this.state.invoiceId}
        />
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

//FormList = Form.create()(FormList);

export default Form.create()(CostDetail);

//export default connect(mapStateToProps, null, null, { withRef: true })(injectIntl(FormList));

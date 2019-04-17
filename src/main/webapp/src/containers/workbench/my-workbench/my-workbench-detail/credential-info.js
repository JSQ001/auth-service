import React, { Component } from 'react';
import Table from 'widget/table';
import { Button, DatePicker, Card, Popover, message } from 'antd';
import PropTypes from 'prop-types';
import moment from 'moment';
import httpFetch from 'share/httpFetch';

class CredentialTable extends Component {
  constructor(props) {
    super(props);
    this.state = {
      time: moment(new Date()),
      columns: [
        {
          title: '说明',
          dataIndex: 'num',
          align: 'center',
        },
        {
          title: '凭证日期',
          dataIndex: 'transferDate',
          align: 'center',
          width: 150,
          render: text => {
            return (
              <Popover content={moment(text).format('YYYY-MM-DD')}>
                {moment(text).format('YYYY-MM-DD')}
              </Popover>
            );
          },
        },
        {
          title: '公司',
          dataIndex: 'companyName',
          align: 'center',
        },
        {
          title: '责任中心',
          dataIndex: 'responsibilityCenterName',
          align: 'center',
        },
        {
          title: '科目',
          dataIndex: 'subjectName',
          align: 'center',
          width: 120,
        },
        {
          title: '币种',
          dataIndex: 'currencyCode',
          align: 'center',
          width: 120,
        },
        {
          title: '原币借方',
          dataIndex: 'originalCurrencyDebit',
          align: 'center',
          width: 120,
        },
        {
          title: '原币贷方',
          dataIndex: 'originalCurrencyCredit',
          align: 'center',
          width: 120,
        },
        {
          title: '本币借方',
          dataIndex: 'localCurrencyDebit',
          align: 'center',
          width: 120,
        },
        {
          title: '本币贷方',
          dataIndex: 'localCurrencyCredit',
          align: 'center',
          width: 120,
        },
        {
          title: '科目段1',
          dataIndex: 'subjectSection1',
          align: 'center',
        },
        {
          title: '科目段2',
          dataIndex: 'subjectSection2',
          align: 'center',
        },
        {
          title: '科目段3',
          dataIndex: 'subjectSection3',
          align: 'center',
        },
      ],
      dataSource: [],
      page: 0,
      size: 10,
      loading: false,
      pagination: {
        showSizeChanger: true,
        showQuickJumper: true,
        showTotal: (total, range) =>
          this.$t('common.show.total', {
            range0: `${range[0]}`,
            range1: `${range[1]}`,
            total,
          }),
      },
      scrollX: 1800,
      createLoading: false,
      deleteLoading: false,
    };
  }

  componentWillReceiveProps(nextProps) {
    const {
      params: { tenantId },
    } = this.props;
    if (nextProps.params.tenantId !== tenantId) {
      this.getTableList(nextProps);
    }
  }

  // 获取数据
  getTableList = (value = this.props) => {
    const { url, params, queryMethod } = value;
    if (url) {
      const { page, size } = this.state;
      if (queryMethod === 'get') {
        httpFetch
          .get(url, { ...params, page, size })
          .then(res => {
            this.reBuildColumns(res.data);
          })
          .catch(err => {
            message.error(err.response.data.message);
          });
      } else {
        const queryParams = this.queryParams({ ...params, page, size });
        httpFetch
          .post(`${url}?${queryParams}`)
          .then(res => {
            this.reBuildColumns(res.data);
          })
          .catch(err => {
            message.error(err.response.data.message);
          });
      }
    }
  };

  // 把参数转化成URL格式
  queryParams = obj => {
    let result = '';
    for (const item of Object.entries(obj)) {
      result += `&${item[0]}=${item[1]}`;
    }
    return result ? result.substring(1) : '';
  };

  // 动态生成科目段字段
  reBuildColumns = data => {
    // console.log(data);//暂未实现
    this.setState({ dataSource: data });
  };

  // 选择时间
  onChangeTime = momentTime => {
    this.setState({
      time: momentTime,
    });
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
        this.getTableList();
      }
    );
  };

  // 生成凭证
  handleCreate = () => {
    const { time } = this.state;
    const { createUrl, documentId } = this.props;
    let tempTime = time;
    if (!time) {
      tempTime = new Date();
      this.setState({ time: moment(tempTime) });
    }
    const params = {
      accountingDate: moment(tempTime).format('YYYY-MM-DD'),
      reportHeaderId: documentId,
    };
    if (!createUrl) return;
    this.setState({ createLoading: true });
    const createParams = this.queryParams({ ...params });
    httpFetch
      .post(`${createUrl}?${createParams}`)
      .then(() => {
        message.success('操作成功');
        this.setState({ createLoading: false });
        this.getTableList();
      })
      .catch(err => {
        this.setState({ createLoading: false });
        message.error(err.response.data.message);
      });
  };

  // 删除凭证
  handleDelete = () => {
    const { deleteUrl, deleteParams } = this.props;
    if (!deleteUrl) return;
    const queryParams = this.queryParams({ ...deleteParams });
    this.setState({ deleteLoading: true });
    httpFetch
      .delete(`${deleteUrl}?${queryParams}`)
      .then(() => {
        this.setState({ deleteLoading: false });
        message.success('删除成功');
        this.getTableList();
      })
      .catch(err => {
        this.setState({ deleteLoading: false });
        message.error(err.response.data.message);
      });
  };

  render() {
    const { isCreate } = this.props;
    const {
      columns,
      dataSource,
      loading,
      pagination,
      scrollX,
      time,
      createLoading,
      deleteLoading,
    } = this.state;
    return (
      <div className="credential-table">
        <Card title="凭证信息" className="credential-table-card">
          {isCreate && (
            <div className="credential-operations">
              <DatePicker
                onChange={this.onChangeTime}
                placeholder="请选择日期"
                value={time}
                style={{ marginRight: '20px' }}
                getCalendarContainer={node => node.parentNode}
              />
              <Button type="primary" onClick={this.handleCreate} loading={createLoading}>
                生成凭证
              </Button>
              <Button onClick={this.handleDelete} className="del-btn" loading={deleteLoading}>
                删除凭证
              </Button>
            </div>
          )}
          <Table
            columns={columns}
            scroll={{ x: scrollX }}
            onChange={this.tablePageChange}
            dataSource={dataSource}
            pagination={pagination}
            loading={loading}
          />
        </Card>
      </div>
    );
  }
}
CredentialTable.propTypes = {
  isCreate: PropTypes.bool,
  // url: PropTypes.string,
};

CredentialTable.defaultProps = {
  isCreate: true,
};
export default CredentialTable;

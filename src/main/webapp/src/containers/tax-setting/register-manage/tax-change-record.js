/* eslint-disable react/destructuring-assignment */
/* eslint-disable no-sequences */
/* eslint-disable no-unused-expressions */
/* eslint-disable no-param-reassign */
/* eslint-disable array-callback-return */
/* eslint-disable react/no-unused-state */
import React from 'react';
import { connect } from 'dva';
import Table from 'widget/table';
import registerApplyService from './tax-register-apply.service';

class ChangeRecord extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      excelVisible: false,
      newShow: false,
      record: {},
      data: [],
      timestamp: new Date().valueOf(),
      columns: [
        {
          /* 变更字段 */
          title: '变更字段',
          dataIndex: 'changeField',
          align: 'center',
        },
        {
          /* 变更前 */
          title: '变更前',
          dataIndex: 'beforeChange',
          align: 'center',
        },
        {
          /* 变更后 */
          title: '变更后',
          dataIndex: 'afterChange',
          align: 'center',
        },
        {
          /* 变更时间 */
          title: '变更时间',
          dataIndex: 'changeTime',
          align: 'center',
        },
      ],
      searchForm: [],
      pagination: {
        total: 0,
        onChange: this.onChangePager,
        current: 1,
        onShowSizeChange: this.onChangePageSize,
        showSizeChanger: true,
        showQuickJumper: true,
        pageSize: 10,
        showTotal: (total, range) =>
          this.$t(
            { id: 'common.show.total' },
            { range0: `${range[0]}`, range1: `${range[1]}`, total }
          ),
      },
    };
  }

  componentWillMount() {
    this.getList();
  }

  // 获得数据
  getList() {
    const { pagination } = this.state;
    const params = {};
    params.page = pagination.current - 1;
    params.size = pagination.pageSize;
    console.log(this.props.params.id);
    registerApplyService
      .getCangeRecord(this.props.params.id, params)
      .then(response => {
        response.data.map(item => {
          item.key = item.id;
        });
        (pagination.total = Number(response.headers['x-total-count']) || 0),
          this.setState({
            data: response.data,
            loading: false,
            pagination,
          });
      })
      .catch(() => {});
  }

  // 每页多少条
  onChangePageSize = (page, pageSize) => {
    const { pagination } = this.state;
    pagination.pageSize = pageSize;
    pagination.page = page;
    this.setState({ pagination }, () => {
      this.getList();
    });
  };

  // 分页点击
  onChangePager = page => {
    const { pagination } = this.state;
    pagination.current = page;
    this.setState({ pagination }, () => {
      this.getList();
    });
  };

  close = flag => {
    this.setState({ newShow: false, record: {} }, () => {
      // eslint-disable-next-line no-unused-expressions
      flag && this.getList();
    });
  };

  handleCancel = () => {
    // eslint-disable-next-line no-unused-expressions
    this.props.onClose && this.props.onClose(true);
  };

  render() {
    const { columns, data, pagination, loading } = this.state;

    return (
      <div className="tax-administration">
        <div className="Table_div" style={{ backgroundColor: 111 }}>
          <Table
            columns={columns}
            dataSource={data}
            pagination={pagination}
            loading={loading}
            bordered
            size="middle"
          />
        </div>
      </div>
    );
  }
}

function mapStateToProps() {
  return {};
}

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(ChangeRecord);

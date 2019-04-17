import React, { Component } from 'react';
import { Button, Input, Row, Col, message } from 'antd';
import Table from 'widget/table';
import { routerRedux } from 'dva/router';
import { connect } from 'dva';
import Service from './service';

const { Search } = Input;

class WorkTap extends Component {
  constructor(props) {
    super(props);
    this.state = {
      detailColumns: [
        {
          title: '单据编号',
          dataIndex: 'documentNumber',
          align: 'center',
        },
        {
          title: '单据公司',
          dataIndex: 'companyName',
          align: 'center',
        },
        {
          title: '业务类别',
          dataIndex: 'businessTypeName',
          align: 'center',
        },
        {
          title: '单据类型',
          dataIndex: 'documentTypeName',
          align: 'center',
        },
        {
          title: '申请人',
          dataIndex: 'employeeName',
          align: 'center',
        },
        {
          title: '入池日期',
          dataIndex: 'enterDate',
          align: 'center',
        },
        {
          title: '当前业务节点',
          dataIndex: 'currentNodeName',
          align: 'center',
        },
        {
          title: '上一业务节点',
          dataIndex: 'lastNodeName',
          align: 'center',
        },
        {
          title: '上一操作人',
          dataIndex: 'lastOperatorName',
          align: 'center',
        },
        {
          title: '上一操作人工作组',
          dataIndex: 'lastWorkTeamName',
          align: 'center',
        },
        {
          title: '上一操作人完成时间',
          dataIndex: 'lastOperatorDate',
          align: 'center',
        },
      ],
      pagination: {
        showSizeChanger: true,
        showQuickJumper: true,
      },
    };
  }

  componentDidMount() {
    this.search();
  }

  /**
   * 切换开始工作/结束工作
   */
  changeStartWork = () => {
    const { isStartWork, isAutoCheckOrder, workChangeStateCB, isShowLoading } = this.props;
    isShowLoading(true);
    Service.changeWorkState(!isStartWork).then(() => {
      isShowLoading(false);
      workChangeStateCB(!isStartWork, isAutoCheckOrder);
    });
  };

  /**
   * 取单
   */
  startGetOrder = () => {
    const { isShowLoading } = this.props;

    isShowLoading(true);
    Service.startGetOrder()
      .then(res => {
        isShowLoading(false);
        message.success('取单成功！共取到' + res.data + '笔单据！');
        this.search();
      })
      .catch(() => {
        isShowLoading(false);
        message.warn('取单失败');
      });
  };

  /**
   * 自动阅单/取消自动阅单
   */
  autoCheckOrder = () => {
    const { isStartWork, isAutoCheckOrder, workChangeStateCB, isShowLoading } = this.props;
    isShowLoading(true);
    Service.autoCheckOrder(!isAutoCheckOrder)
      .then(() => {
        isShowLoading(false);
        workChangeStateCB(isStartWork, !isAutoCheckOrder);
      })
      .catch(() => {
        isShowLoading(false);
      });
  };

  /**
   * 切换分页
   */
  onChangeCheckedPage = page => {
    const { workTapCallBack, workCurrentPage, workSize } = this.props;

    if (page - 1 !== workCurrentPage) {
      workTapCallBack(page - 1, workSize);
    }
  };

  /**
   * 切换每页显示的条数
   */
  onShowSizeChange = (current, pageSize) => {
    const { workTapCallBack, workCurrentPage } = this.props;
    workTapCallBack(workCurrentPage, pageSize);
  };

  /**
   * 搜索
   */
  search = e => {
    const { setDocumentNumberCB } = this.props;
    setDocumentNumberCB(e);
  };

  /**
   * 点击跳转
   */
  select = record => {
    const { dispatch } = this.props;
    if (record && record.pageContent) {
      dispatch(
        routerRedux.push({
          pathname: `${record.pageContent}/${record.id}/${record.documentId}`,
        })
      );
    }
  };

  render() {
    const { detailColumns, pagination } = this.state;
    const {
      dataSource,
      workTableTotalNum,
      workCurrentPage,
      isStartWork,
      isAutoCheckOrder,
      workSize,
    } = this.props;

    Object.assign(pagination, {
      total: workTableTotalNum,
      pageSize: workSize,
      current: workCurrentPage + 1,
      onChange: this.onChangeCheckedPage,
      onShowSizeChange: this.onShowSizeChange,
      showTotal: total => `共搜到 ${total} 条数据`,
    });

    return (
      <div>
        <Row style={{ marginBottom: 10, marginTop: 10 }}>
          <Col span={18}>
            <Button type="primary" onClick={this.changeStartWork}>
              {isStartWork ? '结束工作' : '开始工作'}
            </Button>
            <Button
              disabled={!isStartWork}
              type="primary"
              style={{ marginLeft: 10 }}
              onClick={this.startGetOrder}
            >
              取单
            </Button>
            <Button
              disabled={!isStartWork}
              type="primary"
              style={{ marginLeft: 10 }}
              onClick={this.autoCheckOrder}
            >
              {isAutoCheckOrder ? '取消自动阅单' : '自动阅单'}
            </Button>
          </Col>
          <Col span={6}>
            <Search
              placeholder="请输入单据编号"
              onSearch={e => {
                this.search(e);
              }}
              enterButton
            />
          </Col>
        </Row>
        <Table
          onRow={record => {
            return {
              onClick: event => {
                this.select(record, event);
              }, // 点击行
            };
          }}
          rowKey={record => record.id}
          dataSource={dataSource}
          columns={detailColumns}
          scroll={{ x: 2200 }}
          pagination={pagination}
          bordered
        />
      </div>
    );
  }
}

export default connect()(WorkTap);

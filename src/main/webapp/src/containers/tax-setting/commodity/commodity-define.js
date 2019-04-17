/**
 * Created by 5716 on 2019/3/7.
 */
import React from 'react';
import { connect } from 'dva';
import { Button, Badge } from 'antd';
import Table from 'widget/table';
import SlideFrame from 'widget/slide-frame';
import SearchArea from 'widget/search-area';
import { messages } from 'utils/utils';
import WrappedCommodityDefine from './new-commodity-define';
import commodityDefineService from './commodity-define.service';

class Commodity extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      data: [],
      timestamp: new Date().valueOf(),
      columns: [
        {
          /* 商品编码 */
          title: '商品编码',
          dataIndex: 'commodityCode',
          key: 'commodityCode',
          align: 'center',
        },
        {
          /* 货物或劳务名称 */
          title: '货物或劳务名称',
          dataIndex: 'commodityName',
          key: 'commodityName',
          align: 'center',
        },
        {
          /* 商品和服务分类简称 */
          title: '商品和服务分类简称',
          dataIndex: 'commodityAbb',
          key: 'commodityAbb',
          align: 'center',
        },
        {
          /* 增值税特殊管理 */
          title: '增值税特殊管理',
          dataIndex: 'vatManagement',
          key: 'vatManagement',
          align: 'center',
        },
        {
          /* 优惠政策标识 */
          title: '优惠政策标识',
          dataIndex: 'preferentialPolicy',
          key: 'preferentialPolicy',
          render: (recode, text) => {
            return (
              <div>
                <Badge status={recode ? 'success' : 'error'} />
                {recode ? '使用' : '不使用'}
              </div>
            );
          },
          align: 'center',
        },
        {
          /* 备注 */
          title: '备注',
          dataIndex: 'remarks',
          key: 'remarks',
          align: 'center',
        },
        {
          /* 是否启用 */
          title: '是否启用',
          dataIndex: 'enabled',
          key: 'enabled',
          render: (recode, text) => {
            return (
              <div>
                <Badge status={recode ? 'success' : 'error'} />
                {recode ? this.$t('common.status.enable') : this.$t('common.status.disable')}
              </div>
            );
          },
          align: 'center',
        },
      ],
      searchForm: [
        {
          type: 'input',
          colSpan: 6,
          id: 'commodityCode',
          label: '商品编码',
        },
        {
          type: 'input',
          colSpan: 6,
          id: 'commodityName',
          label: '货物或劳务名称',
        },
        {
          type: 'input',
          colSpan: 6,
          id: 'commodityAbb',
          label: '商品和服务分类简称',
        },
      ],
      pageSize: 10,
      page: 0,
      pagination: {
        total: 0,
      },
      searchParams: {
        commodityCode: '',
        commodityName: '',
        commodityAbb: '',
      },
      updateParams: {},
      showSlideFrameNew: false,
      loading: true,
    };
  }

  componentWillMount() {
    this.getList();
  }

  // 获得数据
  getList() {
    const params = {};
    params.commodityName = this.state.searchParams.commodityName;
    params.commodityCode = this.state.searchParams.commodityCode;
    params.commodityAbb = this.state.searchParams.commodityAbb;
    params.size = this.state.pageSize;
    params.page = this.state.page;
    commodityDefineService
      .getCommodity(params)
      .then(response => {
        response.data.map(item => {
          item.key = item.id;
        });
        this.setState({
          data: response.data,
          loading: false,
          pagination: {
            total: Number(response.headers['x-total-count'])
              ? Number(response.headers['x-total-count'])
              : 0,
            onChange: this.onChangePager,
            current: this.state.page + 1,
            onShowSizeChange: this.onChangePageSize,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total, range) =>
              this.$t(
                { id: 'common.show.total' },
                { range0: `${range[0]}`, range1: `${range[1]}`, total }
              ),
          },
        });
      })
      .catch(e => {});
  }

  // 每页多少条
  onChangePageSize = (page, pageSize) => {
    if (page - 1 !== this.state.page || pageSize !== this.state.pageSize) {
      this.setState({ page: page - 1, pageSize }, () => {
        this.getList();
      });
    }
  };

  // 分页点击
  onChangePager = page => {
    if (page - 1 !== this.state.page)
      this.setState(
        {
          page: page - 1,
          loading: true,
        },
        () => {
          this.getList();
        }
      );
  };

  // 清空搜索区域
  clear = () => {
    this.setState({
      updateParams: {
        commodityCode: '',
        commodityName: '',
        commodityAbb: '',
      },
    });
  };

  // 搜索
  search = result => {
    const searchParams = {
      commodityCode: result.commodityCode,
      commodityName: result.commodityName,
      commodityAbb: result.commodityAbb,
    };
    this.setState(
      {
        searchParams,
        loading: true,
        page: 0,
        current: 1,
      },
      () => {
        this.getList();
      }
    );
  };

  handleClose = params => {
    this.setState(
      {
        showSlideFrameNew: false,
      },
      () => {
        params && this.getList();
      }
    );
  };

  showSlideNew = flag => {
    this.setState({
      showSlideFrameNew: flag,
    });
  };

  newItemTypeShowSlide = () => {
    const timestamp = new Date().valueOf();
    this.setState(
      {
        timestamp,
        updateParams: {},
      },
      () => {
        this.showSlideNew(true);
      }
    );
  };

  putItemTypeShowSlide = recode => {
    const timestamp = new Date().valueOf();
    this.setState(
      {
        updateParams: recode,
        timestamp,
      },
      () => {
        this.showSlideNew(true);
      }
    );
  };

  render() {
    const {
      columns,
      data,
      pagination,
      searchForm,
      showSlideFrameNew,
      loading,
      updateParams,
      isPut,
      timestamp,
    } = this.state;
    return (
      <div className="commodity-define">
        <div className="searchFrom">
          <SearchArea
            searchForm={searchForm}
            submitHandle={this.search}
            clearHandle={this.clear}
            maxLength={4}
            eventHandle={this.searchEventHandle}
          />
        </div>

        <div className="table-header">
          <div className="table-header-buttons">
            <Button type="primary" onClick={this.newItemTypeShowSlide}>
              {this.$t('common.create')}
            </Button>
          </div>
        </div>
        <div className="Table_div" style={{ backgroundColor: 111 }}>
          <Table
            columns={columns}
            dataSource={data}
            pagination={pagination}
            loading={loading}
            bordered
            onRow={record => ({
              onClick: () => this.putItemTypeShowSlide(record),
            })}
            size="middle"
          />
        </div>

        <SlideFrame
          title={JSON.stringify(this.state.updateParams) === '{}' ? '新建商品编码' : '编辑商品编码'}
          show={showSlideFrameNew}
          afterClose={this.handleCloseNewSlide}
          onClose={this.handleClose}
        >
          <WrappedCommodityDefine
            params={{ updateParams, timestamp }}
            onClose={e => {
              this.handleClose(e);
            }}
          />
        </SlideFrame>
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
)(Commodity);

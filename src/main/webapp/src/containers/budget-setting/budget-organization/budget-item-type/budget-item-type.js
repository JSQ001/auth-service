import React from 'react';
import { connect } from 'dva';
import { Button, Badge } from 'antd';
import Table from 'widget/table';
import SlideFrame from 'widget/slide-frame';
import SearchArea from 'widget/search-area';
import WrappedNewBudgetItemType from 'containers/budget-setting/budget-organization/budget-item-type/new-budget-item-type';
import WrappedPutBudgetItemType from 'containers/budget-setting/budget-organization/budget-item-type/put-budget-item-type';
import budgetItemTypeService from 'containers/budget-setting/budget-organization/budget-item-type/budget-item-type.service';
import 'styles/budget-setting/budget-organization/buget-item-type/budget-item-type.scss';

class BudgetItemType extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      data: [],
      columns: [
        {
          title: this.$t({ id: 'budgetItemType.itemTypeCode' }),
          dataIndex: 'itemTypeCode',
          key: 'itemTypeCode',
          align: 'center',
        },
        {
          title: this.$t({ id: 'budgetItemType.itemTypeName' }),
          dataIndex: 'itemTypeName',
          align: 'center',
          key: 'itemTypeName',
        },
        {
          title: this.$t({ id: 'common.column.status' }),
          dataIndex: 'enabled',
          align: 'center',
          key: 'enabled',
          render: (recode, text) => {
            return (
              <div>
                <Badge status={recode ? 'success' : 'error'} />
                {recode
                  ? this.$t({ id: 'common.status.enable' })
                  : this.$t({ id: 'common.status.disable' })}
              </div>
            );
          },
        },
      ],
      searchForm: [
        {
          type: 'input',
          id: 'itemTypeCode',
          label: this.$t({ id: 'budgetItemType.itemTypeCode' }),
        },
        {
          type: 'input',
          id: 'itemTypeName',
          label: this.$t({ id: 'budgetItemType.itemTypeName' }),
        },
      ],
      pageSize: 10,
      page: 0,
      pagination: {
        total: 0,
      },
      searchParams: {
        itemTypeCode: '',
        itemTypeName: '',
      },
      updateParams: {
        itemTypeCode: '',
        itemTypeName: '',
      },
      showSlideFrameNew: false,
      showSlideFramePut: false,
      loading: true,
    };
  }

  componentWillMount() {
    this.getList();
  }

  //获得数据
  getList() {
    this.setState({ loading: true });
    let params = Object.assign({}, this.state.searchParams);
    for (let paramsName in params) {
      !params[paramsName] && delete params[paramsName];
    }
    params.organizationId = this.props.organization.id || this.props.id;
    params.page = this.state.page;
    params.size = this.state.pageSize;
    budgetItemTypeService.getItemType(params).then(response => {
      response.data.map((item, index) => {
        item.key = item.id;
      });
      let pagination = this.state.pagination;
      pagination.total = Number(response.headers['x-total-count']);
      this.setState({
        data: response.data,
        loading: false,
        pagination: {
          total: Number(response.headers['x-total-count']),
          onChange: this.onChangePager,
          current: this.state.page + 1,
        },
      });
    });
  }

  //分页点击
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

  //清空搜索区域
  clear = () => {
    this.setState({
      searchParams: {
        itemTypeCode: '',
        itemTypeName: '',
      },
    });
  };

  //搜索
  search = result => {
    let searchParams = {
      itemTypeCode: result.itemTypeCode,
      itemTypeName: result.itemTypeName,
    };
    this.setState(
      {
        searchParams: searchParams,
        loading: true,
        page: 0,
        current: 1,
      },
      () => {
        this.getList();
      }
    );
  };

  handleCloseNewSlide = params => {
    params && this.getList();
    this.setState({
      showSlideFrameNew: false,
    });
  };

  handleCloseUpdateSlide = params => {
    this.setState(
      {
        showSlideFramePut: false,
      },
      () => {
        params && this.getList();
      }
    );
  };

  showSlidePut = flag => {
    this.setState({
      showSlideFramePut: flag,
    });
  };

  showSlideNew = flag => {
    this.setState({
      showSlideFrameNew: flag,
    });
  };

  newItemTypeShowSlide = () => {
    this.setState(
      {
        updateParams: {},
      },
      () => {
        this.showSlideNew(true);
      }
    );
  };

  putItemTypeShowSlide = recode => {
    this.setState(
      {
        updateParams: recode,
      },
      () => {
        this.showSlidePut(true);
      }
    );
  };

  render() {
    const {
      columns,
      data,
      pagination,
      searchForm,
      showSlideFramePut,
      showSlideFrameNew,
      loading,
      updateParams,
      isPut,
    } = this.state;
    return (
      <div className="versionsDefine">
        <div className="searchFrom">
          <SearchArea
            searchForm={searchForm}
            submitHandle={this.search}
            clearHandle={this.clear}
            eventHandle={this.searchEventHandle}
          />
        </div>

        <div className="table-header">
          <div className="table-header-title">
            {this.$t({ id: 'common.total' }, { total: `${pagination.total}` })}
          </div>
          <div className="table-header-buttons">
            <Button type="primary" onClick={this.newItemTypeShowSlide}>
              {this.$t({ id: 'common.create' })}
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
          title={this.$t({ id: 'budgetItemType.newItemType' })}
          show={showSlideFrameNew}
          onClose={() => this.showSlideNew(false)}
        >
          <WrappedNewBudgetItemType
            onClose={this.handleCloseNewSlide}
            params={{ ...updateParams, flag: showSlideFrameNew }}
          />
        </SlideFrame>

        <SlideFrame
          title={this.$t({ id: 'budgetItemType.editItemType' })}
          show={showSlideFramePut}
          onClose={() => this.showSlidePut(false)}
        >
          <WrappedPutBudgetItemType
            onClose={this.handleCloseUpdateSlide}
            params={{ ...updateParams, flag: showSlideFramePut }}
          />
        </SlideFrame>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    organization: state.budget.organization,
  };
}

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(BudgetItemType);

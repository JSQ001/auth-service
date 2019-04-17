import React, { Component } from 'react';
import SearchArea from 'widget/search-area';
import CustomTable from 'components/Widget/custom-table';
import SlideFrame from 'widget/slide-frame';
import { connect } from 'dva';
import config from 'config';
import baseService from 'share/base.service';
import { routerRedux } from 'dva/router';
import { Button, Badge, Divider, message, Popover } from 'antd';
import NewBillingType from './new-billing-type';
import NewApportionmentSetting from './apportionment-setting';

class Billing extends Component {
  constructor(props) {
    super(props);
    this.state = {
      sob: {},
      searchForm: [
        {
          type: 'select',
          options: [],
          id: 'setOfBooksId',
          placeholder: this.$t('common.please.select'),
          label: this.$t({ id: 'pre.payment.setOfBookName' }),
          labelKey: 'setOfBooksName',
          valueKey: 'id',
          isRequired: true,
          event: 'SETOFBOOKSID',
          allowClear: false,
          defaultValue: props.company.setOfBooksId,
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'reportTypeCode',
          label: this.$t('billing.type.code'),
          /**报账单类型代码 */ colSpan: 6,
        },
        {
          type: 'input',
          id: 'reportTypeName',
          label: this.$t('billing.type.name'),
          /**报账单类型名称 */ colSpan: 6,
        },
        {
          type: 'select',
          id: 'enabled',
          label: this.$t('common.column.status'),
          event: 'ENABLED',
          options: [
            { value: true, label: this.$t('common.status.enable') },
            { value: false, label: this.$t('common.disabled') },
          ],
          colSpan: 6,
        },
      ],
      columns: [
        {
          title: this.$t({ id: 'pre.payment.setOfBookName' }),
          dataIndex: 'setOfBooksName',
          align: 'center',
          width: 190,
          render: (setOfBooksName, record) => {
            return (
              <span>
                {record.setOfBooksCode}-{record.setOfBooksName}
              </span>
            );
          },
        },
        {
          title: this.$t('billing.type.code'),
          dataIndex: 'reportTypeCode',
          align: 'center',
          render: value => {
            return <Popover content={value}>{value}</Popover>;
          },
        },
        {
          title: this.$t('billing.type.name'),
          dataIndex: 'reportTypeName',
          align: 'center',
          render: value => {
            return <Popover content={value}>{value}</Popover>;
          },
        },
        {
          title: this.$t('billing.expense.associationType') /**关联表单类型 */,
          dataIndex: 'formName',
          align: 'center',
          render: value => {
            return <Popover content={value}>{value}</Popover>;
          },
        },
        {
          title: this.$t('expense.policy.enabled') /**状态 */,
          dataIndex: 'enabled ',
          align: 'center',
          render: (enabled, item) => (
            <Badge
              status={item.enabled == true ? 'success' : 'error'}
              text={
                item.enabled == true
                  ? this.$t('common.status.enable')
                  : this.$t('common.status.disable')
              }
            />
          ),
        },
        {
          title: this.$t('adjust.dimension.set') /**维度设置 */,
          dataIndex: 'name4',
          align: 'center',
          render: (value, record, index) => {
            return (
              <span>
                <a
                  onClick={() => {
                    this.dimensionSetting(record);
                  }}
                >
                  {this.$t('adjust.dimension.set')}
                </a>
              </span>
            );
          },
        },
        {
          title: this.$t('billing.expense.apportionmentSetting') /**分摊设置 */,
          dataIndex: 'name5',
          align: 'center',
          render: (value, record, index) => {
            return (
              <span>
                <a
                  onClick={() => {
                    this.apportionmentSetting(record);
                  }}
                >
                  {this.$t('billing.expense.apportionmentSetting')}
                </a>
              </span>
            );
          },
        },
        {
          title: this.$t('expense.policy.options'),
          dataIndex: 'name6',
          align: 'center',
          render: (value, record, index) => {
            return (
              <span>
                <a
                  onClick={() => {
                    this.edit(record);
                  }}
                >
                  {this.$t('common.edit')}
                </a>
                <Divider type="vertical" />
                <a onClick={e => this.distributionCompany(record)}>
                  {this.$t('chooser.data.distribute.company')}
                </a>
              </span>
            );
          },
        },
      ],
      showSlideFrame: false,
      // 分摊设置侧滑框
      showSlideFrameApportionment: false,
      updateParams: {},
      setOfBooksId: this.props.company.setOfBooksId,
      searchParam: {},
    };
  }

  // 生命周期
  componentDidMount() {
    this.getSetOfBookList();
  }

  //获取账套列表
  getSetOfBookList = () => {
    baseService
      .getSetOfBooksByTenant()
      .then(res => {
        let list = [];
        res.data.map(item => {
          list.push({ value: item.id, label: `${item.setOfBooksCode}-${item.setOfBooksName}` });
        });
        let form = this.state.searchForm;
        form[0].options = list;
        this.setState({ searchForm: form });
      })
      .catch(err => {
        message.error('获取数据失败');
        this.setState({ saveLoading: false });
      });
  };

  handleEvent = (event, value) => {
    const { searchForm } = this.state;
    if (event == 'SETOFBOOKSID') {
      this.setState(
        {
          sob: searchForm[0].options.find(item => item.value === value),
          setOfBooksId: value,
          searchParam: { ...this.state.searchParam, setOfBooksId: value },
        },
        () => {
          this.table.search(this.state.searchParam);
        }
      );
    } else if (event == 'ENABLED') {
      this.setState({ searchParam: { ...this.state.searchParam, enabled: value } }, () => {
        this.table.search(this.state.searchParam);
      });
    }
  };

  // 维度设置
  dimensionSetting = record => {
    this.props.dispatch(
      routerRedux.push({
        pathname: '/document-type-manage/definition-billing-type/dimension-setting/' + record.id,
      })
    );
  };

  // 分摊设置
  apportionmentSetting = record => {
    this.setState({
      updateParams: JSON.parse(JSON.stringify(record)),
      showSlideFrameApportionment: true,
    });
  };

  //分配公司
  distributionCompany = record => {
    this.props.dispatch(
      routerRedux.push({
        pathname: '/document-type-manage/definition-billing-type/distribution-company/' + record.id,
      })
    );
  };

  // 新建
  create = () => {
    this.setState({
      updateParams: {},
      showSlideFrame: true,
    });
  };

  // 编辑
  edit = record => {
    this.setState(
      {
        updateParams: JSON.parse(JSON.stringify(record)),
      },
      () => {
        this.setState({ showSlideFrame: true });
      }
    );
  };

  // 搜索
  search = params => {
    this.setState(
      {
        loading: true,
        page: 0,
        searchParam: params,
      },
      () => {
        this.table.search(params);
      }
    );
  };

  // 清空
  clear = () => {
    const { setOfBooksId } = this.state.searchParam;
    let form = this.state.searchForm;
    if (setOfBooksId) {
      form[0].defaultValue = setOfBooksId;
    } else {
      form[0].defaultValue = this.props.company.setOfBooksId;
    }
    this.setState({ searchForm: form, setOfBooksId: form[0].defaultValue, searchParam: {} });
    this.table.search();
  };

  // 关闭新建编辑侧滑框
  handleCloseSlide = flag => {
    this.setState({ showSlideFrame: false }, () => {
      flag && this.table.search(this.state.searchParam);
    });
  };

  // 关闭分摊设置侧滑框
  handleCloseSlideApportionment = flag => {
    this.setState({ showSlideFrameApportionment: false }, () => {
      flag && this.table.search(this.state.searchParam);
    });
  };

  render() {
    const {
      searchForm,
      columns,
      showSlideFrameApportionment,
      showSlideFrame,
      updateParams,
      setOfBooksId,
      sob,
    } = this.state;
    return (
      <div>
        <SearchArea
          searchForm={searchForm}
          submitHandle={this.search}
          clearHandle={this.clear}
          eventHandle={this.handleEvent}
        />
        <Button
          style={{ margin: '15px 0' }}
          className="create-btn"
          type="primary"
          onClick={this.create}
        >
          {this.$t('common.create')}
        </Button>
        <CustomTable
          columns={columns}
          url={`${config.expenseUrl}/api/expense/report/type/query?setOfBooksId=${setOfBooksId}`}
          ref={ref => (this.table = ref)}
        />
        {/* 新建编辑侧滑框 */}
        <SlideFrame
          title={
            JSON.stringify(updateParams) === '{}'
              ? this.$t('expense.create.billing.type')
              : this.$t('expense.edit.billing.type')
          }
          show={showSlideFrame}
          onClose={() => this.setState({ showSlideFrame: false })}
        >
          <NewBillingType
            params={{ ...updateParams }}
            close={this.handleCloseSlide}
            setOfBooks={searchForm[0].options}
            setOfBooksId={setOfBooksId}
            sob={sob}
          />
        </SlideFrame>
        {/* 分摊设置侧滑框 */}
        <SlideFrame
          title={this.$t('billing.expense.apportionmentSetting')}
          show={showSlideFrameApportionment}
          onClose={() => this.setState({ showSlideFrameApportionment: false })}
        >
          <NewApportionmentSetting
            params={{ ...updateParams }}
            close={this.handleCloseSlideApportionment}
          />
        </SlideFrame>
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

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(Billing);

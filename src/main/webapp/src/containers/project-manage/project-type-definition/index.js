import React, { Component } from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import SearchArea from 'widget/search-area';
import CustomTable from 'widget/custom-table';
import SlideFrame from 'widget/slide-frame';
import baseService from 'share/base.service';
import config from 'config';
import { Button, Divider, Badge, message } from 'antd';
import NewProjectDefinition from './new-project-type-definition';

class ProjectTypeDefinition extends Component {
  constructor(props) {
    super(props);
    this.state = {
      searchForm: [
        {
          type: 'select',
          id: 'setOfBooksId',
          label: this.$t('chooser.data.setOfBooks') /* 账套 */,
          options: [],
          isRequired: true,
          event: 'setOfBooksId',
          allowClear: false,
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'projectReqTypeCode',
          colSpan: 6,
          label: this.$t('contract.project.type.code') /* 项目申请单类型代码 */,
        },
        {
          type: 'input',
          id: 'projectReqTypeName',
          colSpan: 6,
          label: this.$t('contract.project.type.name') /* 项目申请单类型名称 */,
        },
        {
          type: 'select',
          id: 'enabled',
          label: this.$t('common.column.status') /* 状态 */,
          colSpan: 6,
          options: [
            { label: this.$t('common.status.enable') /* 启用 */, value: true },
            { label: this.$t('common.status.disable') /* 禁用 */, value: false },
          ],
        },
      ],
      setOfBooksIdList: [],
      setOfBooksId: Number(props.match.params.setOfBooksId)
        ? props.match.params.setOfBooksId
        : props.company.setOfBooksId,
      columns: [
        {
          title: this.$t('contract.project.type.code') /* 项目申请单类型代码 */,
          dataIndex: 'projectReqTypeCode',
          align: 'center',
        },
        {
          title: this.$t('contract.project.type.name') /* 项目申请单类型名称 */,
          dataIndex: 'projectReqTypeName',
          align: 'center',
        },
        {
          title: this.$t('contract.project.relevance.approve') /* 关联审批流 */,
          dataIndex: 'formName',
          align: 'center',
        },
        {
          title: this.$t('common.column.status') /* 状态 */,
          dataIndex: 'enabled',
          align: 'center',
          render: value => {
            return (
              <Badge
                status={value ? 'success' : 'error'}
                text={
                  value
                    ? this.$t('common.status.enable') /* 启用 */
                    : this.$t('common.status.disable') /* 禁用 */
                }
              />
            );
          },
          width: 200,
        },
        {
          title: this.$t('common.operation'),
          dataIndex: 'operation',
          align: 'center',
          render: (value, record) => {
            return (
              <span>
                <a onClick={() => this.edit(record)}>{this.$t('common.edit')}</a>
                <Divider type="vertical" />
                <a onClick={() => this.detailClick(record)}>
                  {this.$t('chooser.data.distribute.company') /* 分配公司 */}
                </a>
              </span>
            );
          },
          width: 200,
        },
      ],
      visible: false,
      params: {},
    };
  }

  componentDidMount() {
    this.getSetOfBooks();
  }

  // 获取账套
  getSetOfBooks = () => {
    baseService
      .getSetOfBooksByTenant()
      .then(res => {
        const { searchForm, setOfBooksId } = this.state;
        const setOfBooksIdList = res.data.map(item => {
          return {
            label: `${item.setOfBooksCode}-${item.setOfBooksName}`,
            value: item.id,
          };
        });
        searchForm[0].options = setOfBooksIdList;
        searchForm[0].defaultValue = setOfBooksId;
        this.setState({ searchForm, setOfBooksIdList });
      })
      .catch(err => message.error(err.response.data.message));
  };

  // 账套切换事件
  handleEvent = (event, value) => {
    if (event === 'setOfBooksId') {
      const { searchForm } = this.state;
      searchForm[0].defaultValue = value;
      this.setState({ setOfBooksId: value, searchForm });
    }
  };

  // 搜索
  search = value => {
    this.table.search(value);
  };

  // 编辑
  edit = record => {
    this.setState({ visible: true, params: record });
  };

  // 分配公司
  detailClick = record => {
    const { setOfBooksId } = this.state;
    this.props.dispatch(
      routerRedux.replace({
        pathname: `/document-type-manage/project-type-definition/distribute-company/${setOfBooksId}/${
          record.id
        }`,
      })
    );
  };

  // 新建
  create = () => {
    this.setState({ visible: true });
  };

  // 侧滑框关闭
  close = isRefresh => {
    this.setState({ visible: false, params: {} });
    if (isRefresh) {
      this.table.search();
    }
  };

  render() {
    const { searchForm, columns, setOfBooksId, visible, setOfBooksIdList, params } = this.state;

    return (
      <div>
        <SearchArea
          searchForm={searchForm}
          eventHandle={this.handleEvent}
          submitHandle={this.search}
        />

        <div style={{ margin: '15px 0' }}>
          <Button onClick={this.create} type="primary">
            {this.$t('common.create')}
          </Button>
        </div>

        <CustomTable
          columns={columns}
          url={`${
            config.contractUrl
          }/api/project/requisition/type/pageByCondition?setOfBooksId=${setOfBooksId}`}
          ref={ref => (this.table = ref)}
        />

        <SlideFrame
          title={
            params.id
              ? this.$t('contract.project.type.edit') /* 编辑项目申请单类型 */
              : this.$t('contract.project.type.new') /* 新建项目申请单类型 */
          }
          show={visible}
          onClose={() => this.close()}
        >
          <NewProjectDefinition
            params={params}
            setOfBooksIdList={setOfBooksIdList}
            setOfBooksId={setOfBooksId}
            onClose={this.close}
          />
        </SlideFrame>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    company: state.user.company,
  };
}

export default connect(mapStateToProps)(ProjectTypeDefinition);

import React, { Component } from 'react';
import { connect } from 'dva';
import SearchArea from 'widget/search-area';
import CustomTable from 'widget/custom-table';
import SlideFrame from 'widget/slide-frame';
import { Button, Divider, Tag, message } from 'antd';
import baseService from 'share/base.service';
import { routerRedux } from 'dva/router';
import config from 'config';
import NewRuleDefinition from './new-rule-definition';

class RuleDefinition extends Component {
  constructor(props) {
    super(props);
    this.state = {
      searchForm: [
        {
          type: 'select',
          colSpan: 6,
          id: 'setOfBookId',
          label: this.$t('chooser.data.setOfBooks') /* 账套 */,
          options: [],
          isRequired: true,
          event: 'setOfBooksId',
          allowClear: false,
        },
        {
          type: 'select',
          colSpan: 6,
          id: 'businessTypeId',
          label: this.$t('workbench.rule.businessType') /* 业务类型 */,
          options: [],
          method: 'get',
          getUrl: `${config.workbenchUrl}/api/workbench/businessType/query/system/type`,
          labelKey: 'businessTypeName',
          valueKey: 'id',
        },
      ],
      columns: [
        {
          title: this.$t('chooser.data.setOfBooks') /* 账套 */,
          dataIndex: 'setOfBookName',
          align: 'center',
        },
        {
          title: this.$t('workbench.rule.businessType') /* 业务类型 */,
          dataIndex: 'businessTypeName',
          align: 'center',
        },
        {
          title: this.$t('workbench.rule.execute') /* 完成时执行 */,
          dataIndex: 'procedureName',
          align: 'center',
        },
        {
          title: this.$t('common.column.status') /* 状态 */,
          dataIndex: 'enabled',
          align: 'center',
          render: value =>
            value ? (
              <Tag color="green">{this.$t('common.status.enable')}</Tag>
            ) : (
              <Tag color="red">{this.$t('common.status.disable')}</Tag>
            ),
          width: '200px',
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
                <a onClick={() => this.detailClick(record)}>{this.$t('acp.detail')}</a>
              </span>
            );
          },
          width: '200px',
        },
      ],
      setOfBooksId: Number(props.match.params.setOfBooksId)
        ? props.match.params.setOfBooksId
        : props.company.setOfBooksId,
      setOfBooksIdList: [],
      showSlideFrame: false,
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
      .catch(err => message.error(err.response.data));
  };

  // 搜索
  search = value => {
    this.table.search(value);
  };

  // 搜索事件
  handleEvent = (event, value) => {
    if (event === 'setOfBooksId') {
      const { searchForm } = this.state;
      searchForm[0].defaultValue = value;
      this.setState({ setOfBooksId: value, searchForm });
    }
  };

  // 编辑
  edit = record => this.setState({ showSlideFrame: true, params: record });

  // 详情跳转
  detailClick = record => {
    this.props.dispatch(
      routerRedux.replace({
        pathname: `/workbench/rule-definition/rule-definition-details/${this.state.setOfBooksId}/${
          record.businessTypeId
        }/${record.id}`,
      })
    );
  };

  // 侧拉框关闭
  close = flag => {
    this.setState({ showSlideFrame: false, params: {} });
    flag && this.table.search();
  };

  render() {
    const {
      searchForm,
      columns,
      setOfBooksId,
      setOfBooksIdList,
      showSlideFrame,
      params,
    } = this.state;

    return (
      <div>
        <SearchArea
          searchForm={searchForm}
          submitHandle={this.search}
          eventHandle={this.handleEvent}
        />

        <div style={{ margin: '15px 0' }}>
          <Button type="primary" onClick={() => this.setState({ showSlideFrame: true })}>
            {this.$t('common.create') /* 新建 */}
          </Button>
        </div>

        <CustomTable
          columns={columns}
          url={`${
            config.workbenchUrl
          }/api/workbench/businessRule/query?setOfBookId=${setOfBooksId}`}
          ref={ref => (this.table = ref)}
        />

        <SlideFrame
          title={
            params.id
              ? this.$t('workbench.rule.edit') /* 编辑作业规则 */
              : this.$t('workbench.rule.new') /* 新建作业规则 */
          }
          show={showSlideFrame}
          onClose={() => this.close()}
        >
          <NewRuleDefinition
            params={params}
            setOfBooksIdList={setOfBooksIdList}
            setOfBooksId={setOfBooksId}
            onClose={this.close}
            toDetail={this.detailClick}
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

export default connect(mapStateToProps)(RuleDefinition);

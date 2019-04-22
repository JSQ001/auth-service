import React from 'react';
import { connect } from 'dva';

import { Tabs, Button, message, Icon, Checkbox, Badge } from 'antd';
import Table from 'widget/table';
const TabPane = Tabs.TabPane;

import httpFetch from 'share/httpFetch';
import config from 'config';

import selectorData from 'share/chooserData';
import ListSelector from 'widget/list-selector';
import BasicInfo from 'widget/basic-info';
import { routerRedux } from 'dva/router';

class BudgetJournalTypeDetail extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      updateState: false,
      saving: false,
      loading: true,
      editing: false,
      infoList: [
        {
          type: 'input',
          label: this.$t('expense.budget.journal.type.code'),
          id: 'journalTypeCode',
          message: this.$t('expense.please.enter.the'),
          disabled: true,
        } /*请输入*/ /*预算日记账类型代码*/,
        {
          type: 'input',
          label: this.$t('expense.budget.journal.type.name'),
          id: 'journalTypeName',
          message: this.$t('expense.please.enter.the'),
          isRequired: true,
        } /*请输入*/ /*预算日记账类型名称*/,
        {
          type: 'value_list',
          labelKey: 'formName',
          valueKey: 'formOid',
          label: this.$t('expense.budget.business.types'),
          bgt: true,
          id: 'businessType',
          message: this.$t('expense.please.select.a'),
          options: [],
          valueListCode: 2018,
          isRequired: true,
        } /*请选择*/ /*预算业务类型*/,
        {
          type: 'select',
          label: this.$t('expense.associated.with.the.form'),
          bgt: true,
          id: 'form0id',
          message: this.$t('expense.please.select.a'),
          options: [],
          labelKey: 'formName',
          valueKey: 'formOid',
        } /*请选择*/ /*关联表单*/,
        { type: 'switch', label: this.$t('expense.policy.enabled'), id: 'enabled' } /*状态*/,
      ],
      tabs: [
        { key: 'STRUCTURE', name: this.$t('expense.general.budget') } /*预算表*/,
        { key: 'ITEM', name: this.$t('expense.project.budget') } /*预算项目*/,
        { key: 'COMPANY', name: this.$t('expense.distribution.of.the.company') } /*公司分配*/,
      ],
      typeData: {},
      data: [],
      tabsData: {
        STRUCTURE: {
          saveUrl: `${config.budgetUrl}/api/budget/journal/type/assign/structures/batch`,
          url: `${config.budgetUrl}/api/budget/journal/type/assign/structures/query`,
          selectorItem: selectorData['budget_journal_structure'],
          extraParams: {
            organizationId: this.props.organization.id,
            journalTypeId: this.props.match.params.id,
          },
          columns: [
            {
              title: this.$t('expense.general.budget.code'),
              dataIndex: 'structureCode',
              width: '40%',
            } /*预算表代码*/,
            {
              title: this.$t('expense.general.budget'),
              dataIndex: 'structureName',
              width: '30%',
            } /*预算表*/,
            {
              title: this.$t('expense.the.default'),
              dataIndex: 'defaultFlag',
              width: '15%',
              render: (defaultFlag, record) => (
                <Checkbox
                  onChange={e => this.onChangeDefault(e, record)}
                  checked={record.defaultFlag}
                />
              ),
            } /*默认*/,
            {
              title: this.$t('expense.enable'),
              key: 'enabled',
              width: '15%',
              render: (enabled, record) => (
                <Checkbox
                  onChange={e => this.onChangeStructureEnabled(e, record)}
                  checked={record.enabled}
                />
              ),
            } /*启用*/,
          ],
        },
        ITEM: {
          saveUrl: `${config.budgetUrl}/api/budget/journal/type/assign/items/batch`,
          url: `${config.budgetUrl}/api/budget/journal/type/assign/items/query`,
          selectorItem: selectorData['budget_journal_item'],
          extraParams: {
            organizationId: this.props.organization.id,
            journalTypeId: this.props.match.params.id,
          },
          columns: [
            {
              title: this.$t('expense.budget.project.code'),
              dataIndex: 'itemCode',
              width: '30%',
            } /*预算项目代码*/,
            {
              title: this.$t('expense.budget.for.the.project.name'),
              dataIndex: 'itemName',
              width: '50%',
            } /*预算项目名称*/,
            {
              title: this.$t('expense.enable'),
              key: 'enabled',
              width: '20%',
              render: (enabled, record) => (
                <Checkbox
                  onChange={e => this.onChangeItemEnabled(e, record)}
                  checked={record.enabled}
                />
              ),
            } /*启用*/,
          ],
        },
        COMPANY: {
          url: `${config.budgetUrl}/api/budget/journal/type/assign/companies/query`,
          saveUrl: `${config.budgetUrl}/api/budget/journal/type/assign/companies/batch`,
          selectorItem: selectorData['budget_journal_company'],
          extraParams: { journalTypeId: this.props.match.params.id },
          columns: [
            {
              title: this.$t('billing.expense.companycode'),
              dataIndex: 'companyCode',
              width: '25%',
            } /*公司代码*/,
            {
              title: this.$t('billing.expense.companyname'),
              dataIndex: 'companyName',
              width: '30%',
            } /*公司名称*/,
            {
              title: this.$t('billing.expense.companytype'),
              dataIndex: 'companyTypeName',
              width: '25%',
            } /*公司类型*/,
            {
              title: this.$t('expense.enable'),
              dataIndex: 'enabled',
              width: '20%' /*启用*/,
              render: (enabled, record) => (
                <Checkbox
                  onChange={e => this.onChangeCompanyEnabled(e, record)}
                  checked={record.enabled}
                />
              ),
            },
          ],
        },
      },
      pagination: {
        total: 0,
      },
      page: 0,
      pageSize: 10,
      nowStatus: 'STRUCTURE',
      showListSelector: false,
      newData: [],
    };
  }

  onChangeDefault = (e, record) => {
    this.setState({ loading: true });
    record.defaultFlag = e.target.checked;
    httpFetch
      .put(`${config.budgetUrl}/api/budget/journal/type/assign/structures`, record)
      .then(() => {
        this.getList(this.state.nowStatus).then(() => {
          this.setState({ loading: false });
        });
      });
  };

  onChangeStructureEnabled = (e, record) => {
    this.setState({ loading: true });
    record.enabled = e.target.checked;
    httpFetch
      .put(`${config.budgetUrl}/api/budget/journal/type/assign/structures`, record)
      .then(() => {
        this.getList(this.state.nowStatus).then(() => {
          this.setState({ loading: false });
        });
      });
  };

  onChangeItemEnabled = (e, record) => {
    this.setState({ loading: true });
    record.enabled = e.target.checked;
    httpFetch.put(`${config.budgetUrl}/api/budget/journal/type/assign/items`, record).then(() => {
      this.getList(this.state.nowStatus).then(() => {
        this.setState({ loading: false });
      });
    });
  };

  onChangeCompanyEnabled = (e, record) => {
    this.setState({ loading: true });
    record.enabled = e.target.checked;
    httpFetch
      .put(`${config.budgetUrl}/api/budget/journal/type/assign/companies`, record)
      .then(() => {
        this.getList(this.state.nowStatus).then(() => {
          this.setState({ loading: false });
        });
      });
  };

  componentWillMount() {
    let setOfBooksId = this.props.setOfBooksId;
    if (!setOfBooksId) {
      setOfBooksId = 0;
    }
    httpFetch
      .get(
        `${
          config.workflowUrl
        }/api/custom/forms/setOfBooks/my/available/all?formTypeId=801002&setOfBooksId=${setOfBooksId}`
      )
      .then(res => {
        let strategyGroup = [];
        res.data.map(item => {
          let strategy = {
            id: item.formId,
            key: item.formName,
            label: item.formName,
            value: item.formOid,
            title: item.formName,
          };
          strategyGroup.push(strategy);
        });
        let infoList = this.state.infoList;
        infoList[3].options = strategyGroup;
        this.setState({ infoList });
      });

    httpFetch
      .get(`${config.budgetUrl}/api/budget/journal/types/${this.props.match.params.id}`)
      .then(response => {
        let data = response.data;
        data.businessType = { label: data.businessTypeName, value: data.businessType };
        data.form0id = data.form0id ? { label: data.formName, value: data.form0id } : '';
        // data.businessType = { label: data.businessTypeName, value: data.businessType };
        // data.form0id = { label: data.formName, value: data.form0id };
        let infoList = this.state.infoList;
        infoList[2].disabled = data.usedFlag;
        this.setState({ typeData: data, infoList });
      });
    this.getList(this.state.nowStatus);

    if (this.props.organization.id) {
      let tabsData = this.state.tabsData;
      tabsData['ITEM'].selectorItem.searchForm[2].getParams = tabsData[
        'ITEM'
      ].selectorItem.searchForm[3].getParams = { organizationId: this.props.organization.id };
      tabsData['STRUCTURE'].selectorItem.searchForm[2].getParams = tabsData[
        'STRUCTURE'
      ].selectorItem.searchForm[3].getParams = { organizationId: this.props.organization.id };
      this.setState({ tabsData });
    }
  }

  //渲染Tabs
  renderTabs() {
    return this.state.tabs.map(tab => {
      return <TabPane tab={tab.name} key={tab.key} />;
    });
  }

  onChangePager = page => {
    if (page - 1 !== this.state.page)
      this.setState(
        {
          page: page - 1,
          loading: true,
        },
        () => {
          this.getList(this.state.nowStatus);
        }
      );
  };

  getList = key => {
    const { tabsData, page, pageSize } = this.state;
    let url = tabsData[key].url;
    if (url) {
      return httpFetch
        .get(`${url}?journalTypeId=${this.props.match.params.id}&page=${page}&size=${pageSize}`)
        .then(response => {
          response.data.map((item, index) => {
            item.key = item.id ? item.id : index;
          });
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
  };

  onChangeTabs = key => {
    this.setState(
      {
        nowStatus: key,
        loading: true,
        page: 0,
      },
      () => {
        this.getList(key);
      }
    );
  };

  handleNew = () => {
    this.setState({ showListSelector: true });
  };

  handleAdd = result => {
    this.setState(
      {
        newData: result.result,
        showListSelector: false,
      },
      () => {
        this.handleSave();
      }
    );
  };

  handleCancel = () => {
    this.setState({ showListSelector: false });
  };

  handleSave = () => {
    const { tabsData, nowStatus, newData } = this.state;
    let paramList = [];
    newData.map(item => {
      if (nowStatus === 'STRUCTURE') {
        item.structureId = item.id;
        delete item.id;
      }
      if (nowStatus === 'ITEM') {
        item.bgtItemId = item.id;
        delete item.id;
      }
      if (nowStatus === 'COMPANY') {
        item = {
          companyId: item.id,
          companyCode: item.code,
          enabled: true,
        };
      }
      item.journalTypeId = this.props.match.params.id;
      paramList.push(item);
    });
    this.setState({ saving: true }, () => {
      httpFetch.post(tabsData[nowStatus].saveUrl, paramList).then(response => {
        message.success(this.$t('expense.add.a.success')); /*添加成功*/
        this.setState(
          {
            newData: [],
            page: 0,
            saving: false,
          },
          () => {
            this.getList(nowStatus);
          }
        );
      });
    });
  };

  updateHandleInfo = params => {
    params.form0id = params.form0id || '';
    this.setState({ editing: true });
    httpFetch
      .put(
        `${config.budgetUrl}/api/budget/journal/types`,
        Object.assign({}, this.state.typeData, params)
      )
      .then(response => {
        message.success(this.$t('common.update.success'));
        let data = response.data;
        data.businessType = { label: data.businessTypeName, value: data.businessType };
        //data.form0id = data.form0id ? { label: data.formName, value: data.form0id } : "";
        let infoList = this.state.infoList;
        infoList[2].disabled = data.usedFlag;
        this.setState({
          typeData: data,
          updateState: true,
          editing: false,
          infoList,
        });
      })
      .catch(e => {
        this.setState({ editing: false });
      });
  };

  render() {
    const {
      infoList,
      typeData,
      tabsData,
      loading,
      pagination,
      nowStatus,
      data,
      showListSelector,
      saving,
      updateState,
      editing,
    } = this.state;
    return (
      <div>
        <BasicInfo
          infoList={infoList}
          infoData={typeData}
          updateHandle={this.updateHandleInfo}
          updateState={updateState}
          loading={editing}
        />
        <Tabs onChange={this.onChangeTabs} style={{ marginTop: 20 }}>
          {this.renderTabs()}
        </Tabs>
        <div className="table-header">
          <div className="table-header-title">
            {this.$t('budget.desc.code2', { total: pagination.total })}
          </div>
          <div className="table-header-buttons">
            <Button type="primary" onClick={this.handleNew} loading={saving}>
              {this.$t('expense.addition')}
            </Button>
            {/*添 加*/}
          </div>
        </div>
        <Table
          columns={tabsData[nowStatus].columns}
          dataSource={data}
          pagination={pagination}
          loading={loading}
          bordered
          size="middle"
        />

        <a
          className="back"
          onClick={() => {
            this.props.dispatch(
              routerRedux.push({
                pathname: '/budget-setting/budget-organization/budget-organization-detail/:setOfBooksId/:id/:tab'
                  .replace(':id', this.props.match.params.orgId)
                  .replace(':setOfBooksId', this.props.match.params.setOfBooksId)
                  .replace(':tab', 'JOURNAL_TYPE'),
              })
            );
          }}
        >
          <Icon type="rollback" style={{ marginRight: '5px' }} />
          {this.$t('expense.return')}/*返回*/
        </a>

        <ListSelector
          visible={showListSelector}
          onOk={this.handleAdd}
          onCancel={this.handleCancel}
          selectorItem={tabsData[nowStatus].selectorItem}
          extraParams={tabsData[nowStatus].extraParams ? tabsData[nowStatus].extraParams : {}}
        />
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
)(BudgetJournalTypeDetail);

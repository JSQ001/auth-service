/**
 * created by jsq on 2018/12/26
 */
import React from 'react';
import { connect } from 'dva';
import { Button, Popover, message, Tabs, Divider, Popconfirm } from 'antd';
import { routerRedux } from 'dva/router';
import SearchArea from 'widget/search-area';
import NewParameterDefinition from 'containers/setting/parameter-definition/new-parameter-definition';
const TabPane = Tabs.TabPane;
import config from 'config';
import CustomTable from 'widget/custom-table';
import parameterService from 'containers/setting/parameter-definition/parameter-definition.service';
import SlideFrame from 'widget/slide-frame';
import sobService from 'containers/finance-setting/set-of-books/set-of-books.service';
import moment from 'moment';

class ParameterCompany extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      record: {},
      company: props.company,
      searchParams: {
        parameterLevel: 'COMPANY',
        companyId: props.company.id,
      },
      visible: false,
      searchForm: [
        {
          type: 'list',
          id: 'companyId',
          label: this.$t({ id: 'exp.company' }),
          listType: 'enableCompanyByTenant',
          options: [],
          labelKey: 'name',
          valueKey: 'id',
          colSpan: 6,
          clear: false,
          event: 'COMPANY',
          single: true,
          listExtraParams: { tenantId: props.company.tenantId },
          defaultValue: [{ id: props.company.id, name: props.company.name }],
        },
        {
          type: 'select',
          id: 'moduleCode',
          label: this.$t({ id: 'parameter.definition.model' }),
          options: [],
          labelKey: 'moduleName',
          valueKey: 'moduleCode',
          colSpan: 6,
          event: 'MODULE',
          getUrl: `${config.mdataUrl}/api/parameter/module`,
          method: 'get',
        },
        {
          type: 'input',
          id: 'parameterCode',
          colSpan: 6,
          label: this.$t({ id: 'budget.parameterCode' }),
        } /*参数代码*/,
        {
          type: 'input',
          id: 'parameterName',
          colSpan: 6,
          label: this.$t({ id: 'budget.parameterName' }),
        } /*参数名称*/,
      ],
      columns: [
        {
          title: this.$t({ id: 'exp.company' }),
          key: 'companyName',
          dataIndex: 'companyName',
          align: 'center',
          render: desc => (
            <Popover placement="topLeft" content={desc}>
              {desc || '-'}
            </Popover>
          ),
        },
        {
          /*模块*/
          title: this.$t({ id: 'parameter.definition.model' }),
          key: 'moduleName',
          dataIndex: 'moduleName',
          align: 'center',
          render: desc => (
            <Popover placement="topLeft" content={desc}>
              {desc || '-'}
            </Popover>
          ),
        },
        {
          /*参数代码*/
          title: this.$t({ id: 'budget.parameterCode' }),
          key: 'parameterCode',
          dataIndex: 'parameterCode',
          align: 'center',
        },
        {
          /*参数名称*/
          title: this.$t({ id: 'budget.parameterName' }),
          key: 'parameterName',
          dataIndex: 'parameterName',
          align: 'center',
          render: desc => (
            <Popover placement="topLeft" content={desc}>
              {desc || '-'}
            </Popover>
          ),
        },
        {
          /*参数层级*/
          title: this.$t({ id: 'parameter.level' }),
          key: 'parameterHierarchy',
          dataIndex: 'parameterHierarchy',
          align: 'center',
          render: desc => (
            <Popover placement="topLeft" content={desc}>
              {desc || '-'}
            </Popover>
          ),
        },
        {
          /*参数值*/
          title: this.$t({ id: 'budget.balance.params.value' }),
          key: 'parameterValue',
          dataIndex: 'parameterValue',
          align: 'center',
          render: (desc, record) => {
            desc &&
              record.parameterValueType === 'DATE' &&
              (desc = moment(desc).format('YYYY-MM-DD'));
            return (
              <Popover placement="topLeft" content={desc}>
                {desc || '-'}
              </Popover>
            );
          },
        },
        {
          /*描述*/
          title: this.$t({ id: 'chooser.data.description' }),
          key: 'parameterValueDesc',
          dataIndex: 'parameterValueDesc',
          align: 'center',
          render: desc => (
            <Popover placement="topLeft" content={desc}>
              {desc || '-'}
            </Popover>
          ),
        },
        {
          /*操作*/
          title: this.$t({ id: 'common.operation' }),
          dataIndex: 'operation',
          align: 'center',
          render: (operation, record, index) => {
            return (
              <div>
                <a onClick={e => this.handleEdit(e, record)}>{this.$t('common.edit')}</a>
                <span>
                  <Divider type="vertical" />
                  <Popconfirm
                    title={this.$t('configuration.detail.tip.delete')}
                    onConfirm={e => this.deleteItem(e, record)}
                  >
                    <a>{this.$t('common.delete')}</a>
                  </Popconfirm>
                </span>
              </div>
            );
          },
        },
      ],
    };
  }

  handleEdit = (e, record) => {
    e.preventDefault();
    e.stopPropagation();
    this.setState({
      visible: true,
      record,
    });
  };

  deleteItem = (e, record) => {
    e.preventDefault();
    e.stopPropagation();
    parameterService
      .deleteParameter(record.id)
      .then(res => {
        message.success(this.$t('common.delete.success', { name: '' }));
        this.table.search({
          parameterLevel: 'COMPANY',
          ...this.state.searchParams,
        });
      })
      .catch(e => {
        message.error(this.$t('common.delete.failed'));
      });
  };

  handleSearch = values => {
    this.setState(
      {
        searchParams: {
          ...this.state.searchParams,
          ...values,
        },
      },
      () => {
        this.table.search(values);
      }
    );
  };

  handleAdd = () => {
    this.setState({ visible: true, record: {} });
  };

  handleClose = params => {
    this.setState(
      {
        visible: false,
      },
      () => {
        params &&
          this.table.search({ parameterLevel: 'COMPANY', ...this.state.searchParams }, true);
      }
    );
  };

  handleEvent = (event, value) => {
    switch (event) {
      case 'MODULE': {
        this.setState(
          {
            searchParams: {
              ...this.state.searchParams,
              moduleCode: value,
            },
          },
          () => {
            this.table.search(this.state.searchParams);
          }
        );
        break;
      }

      case 'COMPANY': {
        this.setState(
          {
            company: value[0],
            searchParams: {
              ...this.state.searchParams,
              companyId: value[0].id,
            },
          },
          () => {
            this.table.search(this.state.searchParams);
          }
        );
        break;
      }
    }
  };

  render() {
    const { visible, record, company, searchForm, columns } = this.state;
    return (
      <div className="content-company" style={{ marginTop: 15 }}>
        <SearchArea
          eventHandle={this.handleEvent}
          searchForm={searchForm}
          maxLength={4}
          submitHandle={this.handleSearch}
        />
        <div className="table-header" style={{ marginTop: 15 }}>
          <div className="table-header-buttons">
            <Button type="primary" onClick={this.handleAdd}>
              {this.$t({ id: 'common.add' })}
            </Button>{' '}
            {/*添加*/}
          </div>
        </div>
        <CustomTable
          columns={columns}
          params={{
            parameterLevel: 'COMPANY',
            companyId: this.props.company.id,
          }}
          url={`${config.mdataUrl}/api/parameter/setting/page/by/level/cond`}
          ref={ref => (this.table = ref)}
        />
        <SlideFrame
          title={this.$t('parameter.definition.comp') + this.$t('parameter.definition')}
          show={visible}
          onClose={() => this.setState({ visible: false })}
        >
          <NewParameterDefinition
            params={{ record: record, visible, company: company, nowTab: '2' }}
            onClose={this.handleClose}
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

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(ParameterCompany);

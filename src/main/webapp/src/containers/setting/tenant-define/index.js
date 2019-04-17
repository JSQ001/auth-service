/**
 * Created by jsq on 2019/01/29
 */
import React from 'react';
import config from 'config';
import { connect } from 'dva';
import { Button, Popover } from 'antd';
import SearchArea from 'widget/search-area.js';
import CustomTable from 'components/Widget/custom-table';
import SlideFrame from 'widget/slide-frame';
import NewUpdateTenant from 'containers/setting/tenant-define/new-update-tenant';

class TenantDefine extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      showSlideFrame: false,
      record: {},
      searchForm: [
        {
          type: 'input',
          id: 'tenantCode',
          colSpan: 6,
          label: this.$t('setting.tenant.code') /*租户代码*/,
        },
        {
          type: 'input',
          id: 'tenantName',
          colSpan: 6,
          label: this.$t('setting.tenant.name') /*租户名称*/,
        },
        {
          type: 'input',
          id: 'remark',
          colSpan: 6,
          label: this.$t('common.user.remark') /*用户备注*/,
        },
        {
          type: 'input',
          id: 'userName',
          colSpan: 6,
          label: this.$t('common.user.name') /*用户名称*/,
        },
        {
          type: 'input',
          id: 'mobile',
          colSpan: 6,
          label: this.$t('chooser.data.mobile') /*手机号*/,
        },
        { type: 'input', id: 'email', colSpan: 6, label: this.$t('login.little.email') /*邮箱*/ },
        {
          type: 'input',
          id: 'login',
          colSpan: 6,
          label: this.$t('login.little.account') /*登陆账号*/,
        },
      ],
      columns: [
        {
          /*租户代码*/
          title: this.$t('setting.tenant.code'),
          key: 'tenantCode',
          dataIndex: 'tenantCode',
          width: 140,
        },
        {
          /*租户名称*/
          title: this.$t('setting.tenant.name'),
          key: 'tenantName',
          dataIndex: 'tenantName',
          width: 140,
          render: desc => <Popover content={desc}>{desc || '-'}</Popover>,
        },
        {
          /*用户备注*/
          title: this.$t('common.user.remark'),
          key: 'remark',
          dataIndex: 'remark',
          width: 158,
          render: desc => <Popover content={desc}>{desc || '-'}</Popover>,
        },
        {
          /*用户名称*/
          title: this.$t('common.user.name'),
          key: 'userName',
          dataIndex: 'userName',
          width: 140,
          render: desc => <Popover content={desc}>{desc || '-'}</Popover>,
        },
        {
          /*手机号*/
          title: this.$t('chooser.data.mobile'),
          key: 'mobile',
          dataIndex: 'mobile',
          width: 140,
          render: desc => <Popover content={desc}>{desc || '-'}</Popover>,
        },
        {
          /*邮箱*/
          title: this.$t('login.little.email'),
          key: 'email',
          dataIndex: 'email',
          width: 180,
          render: desc => <Popover content={desc}>{desc || '-'}</Popover>,
        },
        {
          /*登陆账号*/
          title: this.$t('login.little.account'),
          key: 'login',
          dataIndex: 'login',
          width: 180,
          render: desc => <Popover content={desc}>{desc || '-'}</Popover>,
        },
      ],
    };
  }

  // 点击搜索
  handleSearch = values => {
    console.log(values);
    this.table.search(values);
  };

  handleCreate = () => {
    this.setState({
      showSlideFrame: true,
      record: {},
    });
  };

  // 点击行，进入该行详情页面
  handleRowClick = record => {
    console.log(record);
    this.setState({
      showSlideFrame: true,
      record,
    });
  };

  handleAfterClose = params => {
    this.setState(
      {
        showSlideFrame: false,
      },
      () => {
        params && this.table.search();
      }
    );
  };

  render() {
    const { searchForm, showSlideFrame, columns, record } = this.state;
    return (
      <div className="budget-structure">
        <SearchArea searchForm={searchForm} submitHandle={this.handleSearch} maxLength={4} />
        <div className="table-header">
          <div className="table-header-buttons">
            {/*新建*/}
            <Button type="primary" onClick={this.handleCreate}>
              {this.$t({ id: 'common.create' })}
            </Button>
          </div>
        </div>
        <CustomTable
          columns={columns}
          url={`${config.baseUrl}/api/tenant/query/condition`}
          ref={ref => (this.table = ref)}
        />
        <SlideFrame
          title={this.$t(record.id ? 'tenant.update' : 'tenant.new')}
          show={showSlideFrame}
          onClose={() => this.setState({ showSlideFrame: false })}
        >
          <NewUpdateTenant
            onClose={this.handleAfterClose}
            params={{ record: record, visible: showSlideFrame }}
          />
        </SlideFrame>
      </div>
    );
  }
}

export default connect()(TenantDefine);

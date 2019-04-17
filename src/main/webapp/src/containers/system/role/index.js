import React from 'react';
import CustomTable from 'components/Template/custom-table';
import { Divider, Tag, Button, message, Alert } from 'antd';
import moment from 'moment';
import service from './service';
import NewRole from './new';
import SelectMenus from './menus';
import { connect } from 'dva';
import SearchForm from 'components/Template/search-form';

class Role extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      newShow: false,
      allocShow: false,
      roleId: '',
      record: {},
      formItems: [
        {
          label: this.$t({ id: 'chooser.data.code' }), //代码
          dataIndex: 'roleCode',
          type: 'input',
        },
        {
          label: this.$t({ id: 'chooser.data.name' }), //名称
          dataIndex: 'roleName',
          type: 'input',
        },
      ],
      columns: [
        {
          title: this.$t({ id: 'chooser.data.code' }), //代码
          align: 'left',
          width: 150,
          dataIndex: 'roleCode',
        },
        {
          title: this.$t({ id: 'chooser.data.name' }), //名称
          align: 'left',
          width: 150,
          dataIndex: 'roleName',
          tooltips: true,
        },
        {
          title: this.$t({ id: 'my.contract.create.date' }), //创建日期
          align: 'left',
          width: 150,
          dataIndex: 'createdDate',
          render: value => moment(value).format('YYYY-MM-DD'),
        },
        {
          title: this.$t({ id: 'code.rule.status' }), //状态
          align: 'left',
          dataIndex: 'enabled',
          width: 150,
          render: value =>
            !value ? (
              <Tag color="red">{this.$t('common.disabled')}</Tag>
            ) : (
              <Tag color="green">{this.$t('common.enabled')}</Tag>
            ),
        },
        {
          title: this.$t({ id: 'common.operation' }), //操作
          dataIndex: 'option',
          align: 'center',
          render: (value, record) => {
            return (
              <span>
                <a onClick={() => this.alloc(record)}>{this.$t('base.distributiond.menu')}</a>
                {/*分配菜单*/}
                <Divider type="vertical" />
                <a onClick={() => this.edit(record)}>{this.$t('common.edit')}</a>
                {/*编辑*/}
                <Divider type="vertical" />
                <a onClick={() => this.remove(record)}>
                  {record.enabled ? this.$t('common.disabled') : this.$t('common.enabled')}
                </a>
              </span>
            );
          },
        },
      ],
    };
  }

  add = () => {
    this.setState({ newShow: true });
  };

  alloc = record => {
    this.setState({ allocShow: true, roleId: record.id });
  };

  remove = record => {
    service.disableRole({ ...record, enabled: !record.enabled }).then(response => {
      this.table.reload();
      message.success(this.$t('common.operate.success'));
    });
  };

  edit = record => {
    this.setState({ record: { ...record }, newShow: true });
  };

  close = flag => {
    this.setState({ newShow: false, record: {} }, () => {
      flag && this.table.reload();
    });
  };

  search = values => {
    this.table.search(values);
  };

  render() {
    const { columns, newShow, allocShow, roleId, formItems, record } = this.state;
    return (
      <div style={{ backgroundColor: '#fff', padding: 10, overflow: 'auto' }}>
        <Alert
          style={{ marginBottom: 10 }}
          closable
          message={this.$t(
            'base.after.the.operation.refresh.the.current.page.or.re-login.to.take.effect!'
          )} //操作后，刷新当前页面，或者重新登录才能生效！
          type="info"
        />
        <SearchForm formItems={formItems} search={this.search} />
        <Button style={{ margin: '10px 0' }} onClick={this.add} type="primary">
          {this.$t('base.add')}
        </Button>
        <CustomTable
          ref={ref => (this.table = ref)}
          columns={columns}
          url={'/api/role/query/tenant?tenantId=' + this.props.currentUser.tenantId}
        />
        <NewRole params={record} visible={newShow} onClose={this.close} />
        {allocShow && (
          <SelectMenus
            roleId={roleId}
            onCancel={() => {
              this.setState({ allocShow: false });
            }}
            visible={allocShow}
          />
        )}
      </div>
    );
  }
}
export default connect(({ user }) => ({
  currentUser: user.currentUser,
}))(Role);

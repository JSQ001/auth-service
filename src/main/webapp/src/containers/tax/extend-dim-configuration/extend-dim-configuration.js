/* eslint-disable */
import React from 'react';
import { connect } from 'dva';
import config from 'config';
import { Button, message, Popconfirm } from 'antd';
import CustomTable from 'components/Widget/custom-table';
import NewExtendDimConfiguration from './new-extend-dim-configuration';
import SlideFrame from 'widget/slide-frame';
import Service from './service';

class TaxPriceSeparationRules extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      record: {},
      visible: false,
      loading: false,
      data: {},
      pagination: {
        total: 0,
        page: 0,
        pageSize: 10,
      },
      columns: [
        {
          title: '规则代码',
          dataIndex: 'ruleCode',
          align: 'center',
        },
        {
          title: '规则名称',
          dataIndex: 'ruleName',
          align: 'center',
        },
        {
          title: '维度',
          dataIndex: 'dimensionName',
          align: 'center',
        },

        {
          title: '默认值',
          dataIndex: 'defaultItemValueName',
          align: 'center',
        },

        {
          title: '是否必输',
          dataIndex: 'essentialFlag',
          align: 'center',
          render: res => (res === 'Y' ? '是' : '否'),
        },
        {
          title: '优先级',
          dataIndex: 'priority',
          align: 'center',
        },
        {
          title: '匹配表',
          dataIndex: 'tabName',
          align: 'center',
        },
        {
          title: '匹配字段',
          dataIndex: 'colName',
          align: 'center',
        },
        {
          title: '操作',
          dataIndex: 'operation',
          align: 'center',
          render: (value, record) => (
            <div>
              <a onClick={() => this.edit(record)}>编辑</a>
              <span className="ant-divider" />
              <Popconfirm title="确定要删除吗?" onConfirm={() => this.handleDelete(record)}>
                <a>删除</a>
              </Popconfirm>
            </div>
          ),
        },
      ],
      model: {},
    };
  }
  handleDelete = record => {
    const id = record.id;
    Service.delectInvoicingSite(id)
      .then(res => {
        message.success(this.$t('common.delete.success'));
        this.customTable.search(this.state.searchParams);
      })
      .catch(e => {
        message.error(this.$t('common.delete.failed'));
      });
  };

  componentDidMount() {}

  // 新建
  create = () => {
    this.setState({
      visibel: true,
    });
  };
  //编辑
  edit = record => {
    this.setState({ model: JSON.parse(JSON.stringify(record)), visibel: true });
  };
  // //删除
  // delect = record => {
  //   const id = record.id;
  //   Service.delectInvoicingSite(id).then(response => {
  //     message.success(
  //       this.$t({ id: 'common.operate.success' }, { name: values.description })
  //     );
  //   }
  //   ).catch(e => {
  //     if (e.response) {
  //       message.error(this.$t({ id: 'common.save.filed' }) + `,${e.response.data.message}`);
  //     }
  //   })
  // };
  //关闭侧拉框回调
  close = flag => {
    this.setState({ visibel: false, model: {} }, () => {
      if (flag) {
        this.customTable.search();
      }
    });
  };

  render() {
    const { columns, tabValue, model, visibel } = this.state;
    return (
      <div className="train">
        <div style={{ margin: '20px 0' }}>
          {/*新建*/}
          <Button
            style={{ margin: '20px 20px 20px 0' }}
            className="create-btn"
            type="primary"
            onClick={this.create}
          >
            添加维度
          </Button>
          <CustomTable
            columns={columns}
            url={`${config.taxUrl}/api/tax/vat/rule/dimension/query/condition`}
            ref={ref => (this.customTable = ref)}
          />
          <SlideFrame
            show={visibel}
            onClose={() => {
              this.setState({
                visibel: false,
                model: {},
              });
            }}
          >
            <NewExtendDimConfiguration params={model} close={this.close} />
          </SlideFrame>
        </div>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    company: state.user.company,
    user: state.user.currentUser,
  };
}

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(TaxPriceSeparationRules);

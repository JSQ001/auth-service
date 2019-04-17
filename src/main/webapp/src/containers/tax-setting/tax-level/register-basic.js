/*eslint-disable*/
import React from 'react';
import { connect } from 'dva';
import config from 'config';
import { message, Table, Spin } from 'antd';
import CustomTable from 'widget/custom-table';
import TaxLevelService from './tax-level-service';
class RegisterBasic extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      dataSource: [],
      loading: true,
      columns: [
        {
          title: '纳税人名称',
          dataIndex: 'taxpayerName',
          key: 'taxpayerName',
        },
        {
          title: '纳税人识别号',
          dataIndex: 'taxpayerNumber',
          key: 'taxpayerNumber',
        },
        {
          title: '税号类型',
          dataIndex: 'taxpayerNumberTypeName',
          key: 'taxpayerNumberTypeName',
        },
        {
          title: '纳税资质',
          dataIndex: 'taxQualificationName',
          key: 'taxQualificationName',
        },
      ],
    };
  }
  componentDidMount() {
    TaxLevelService.pageTaxValueAddedTaxInfoByCond()
      .then(res => {
        let data = res.data;
        console.log(data);
        let data1 = this.buildTree(data);
        this.setState({
          dataSource: data1,
          loading: false,
        });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  }
  buildTree(data) {
    let temp = {};
    let tree = {};
    data.forEach(item => {
      temp[item.id] = item;
    });

    let tempKeys = Object.keys(temp);
    tempKeys.forEach(key => {
      let item = temp[key];

      if (item.parentTaxpayerId == null) {
        item.parentTaxpayerId = -1;
      }
      let _itemPId = item.parentTaxpayerId;
      let parentItemByPid = temp[_itemPId];
      if (parentItemByPid) {
        if (!parentItemByPid.children) {
          parentItemByPid.children = [];
        }
        parentItemByPid.children.push(item);
      } else {
        tree[item.id] = item;
      }
    });
    return Object.keys(tree).map(key => tree[key]);
  }
  render() {
    let { dataSource, columns } = this.state;
    return (
      <Spin spinning={this.state.loading}>
        <Table
          style={{ marginTop: '15px' }}
          columns={columns}
          dataSource={dataSource}
          rowKey="id"
          // url={`${config.taxUrl}/api/tax/taxRegister/basic/hierarchy/condition`}
          // ref={ref => (this.customTable = ref)}
        />
      </Spin>
    );
  }
}

export default connect()(RegisterBasic);

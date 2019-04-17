/**
 * Created by 5716 on 2019/3/7.
 */
import React from 'react';
import { connect } from 'dva';
import config from 'config';

import { Button, Form, Switch, Input, message, Icon, Select } from 'antd';
import CustomTable from 'widget/custom-table';

const FormItem = Form.Item;
const { Option } = Select;

class TaxClientOther extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      params: {},
      enabled: true,
      isPut: false,
      loading: false,
      // paymentMethodCategoryOptions: [],
      searchFrom: [
        { id: 'enabled' },
        { id: 'commodityCode' },
        { id: 'commodityName' },
        { id: 'commodityAbb' },
      ],
      columns: [
        {
          title: '维度代码',
          dataIndex: 'dimensionCode',
          align: 'center',
        },
        {
          title: '维度名称',
          dataIndex: 'dimensionName',
          align: 'center',
        },
        {
          title: '维值代码',
          dataIndex: 'dimensionValueCode',
          align: 'center',
        },

        {
          title: '维值名称',
          dataIndex: 'dimensionValueName',
          align: 'center',
        },
      ],
    };
  }

  /* componentWillMount() {
    this.getPaymentMethodCategory();
  }

  getPaymentMethodCategory() {
    let paymentMethodCategoryOptions = [];
    this.getSystemValueList(2105).then(res => {
      res.data.values.map(data => {
        paymentMethodCategoryOptions.push({
          label: data.messageKey,
          value: data.value,
          key: data.value,
        });
      });
      this.setState({
        paymentMethodCategoryOptions,
      });
    });
  } */

  componentDidMount() {
    // this.getInfo();
  }

  onCancel = () => {
    this.props.onClose(false);
  };

  switchChange = value => {
    this.setState({ enabled: value });
  };

  render() {
    const { getFieldDecorator } = this.props.form;
    const { params, enabled, isPut, columns } = this.state;
    const formItemLayout = {
      labelCol: { span: 6, offset: 1 },
      wrapperCol: { span: 14, offset: 1 },
    };
    return (
      <div className="tax-client-other">
        <CustomTable
          ref={ref => (this.table = ref)}
          columns={columns}
          url={
            `${config.taxUrl}/api/tax/client/other/pageByCondition?applicationId=` +
            this.props.params.clientData.id
          }
        />
      </div>
    );
  }
}

const WrappedTaxClientOtherDefine = Form.create()(TaxClientOther);
function mapStateToProps() {
  return {};
}
export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(WrappedTaxClientOtherDefine);

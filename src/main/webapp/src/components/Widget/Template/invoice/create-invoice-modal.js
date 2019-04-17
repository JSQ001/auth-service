import React from 'react';
import { connect } from 'dva';
import PropTypes from 'prop-types';
import { Modal, Form } from 'antd';
import 'styles/my-account/create-invoice.scss';
import { rejectPiwik } from 'share/piwik';
import httpFetch from 'share/httpFetch';
import config from 'config';
import CreateInvoice from 'components/Widget/Template/invoice/create-invoice';

class AddInvoice extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      invoiceTypes: {
        fetched: false,
        data: [],
      },
    };
  }

  handleFocusInvoiceType = () => {
    let { invoiceTypes } = this.state;
    let params = {
      tenantId: this.props.company.tenantId,
      setOfBooksId: this.props.company.setOfBooksId,
    };
    if (!invoiceTypes.fetched) {
      httpFetch.get(`${config.expenseUrl}/api/invoice/type/query/for/invoice`, params).then(res => {
        this.setState({
          invoiceTypes: {
            fetched: true,
            data: res.data,
          },
        });
      });
    }
  };

  componentDidMount() {
    // let code = "01,04,3100171320,11111111,,20180531,111111";
    // this.testInvoice(code);
  }

  render() {
    const { invoiceTypes, checking, invoice, canSubmit } = this.state;
    const { getFieldDecorator, getFieldValue } = this.props.form;
    const { onCreate, fromExpense, onBack, visible, onCancel, params } = this.props;
    const formItemLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 17, offset: 0 },
    };
    const layout = {
      labelCol: { span: 4 },
      wrapperCol: { span: 20, offset: 0 },
    };

    const style = {
      marginLeft: -38,
    };

    const inputStyle = {
      width: '97%',
    };
    let invoiceTypeNo = getFieldValue('invoiceTypeNo');
    return (
      <div className="create-invoice">
        {visible && (
          <Modal
            visible={visible}
            width={800}
            bodyStyle={{
              height: '65vh',
              overflowY: 'auto',
            }}
            onCancel={onCancel}
            title="手工录入发票"
            footer={null}
          >
            <CreateInvoice
              {...params}
              saveSuccess={this.props.saveSuccess}
              onCancel={this.props.onCancel}
            />
          </Modal>
        )}
      </div>
    );
  }
}

AddInvoice.propTypes = {
  onCreate: PropTypes.func,
  fromExpense: PropTypes.bool,
  onBack: PropTypes.func,
  createType: PropTypes.number,
  digitalInvoice: PropTypes.any,
};

function mapStateToProps(state) {
  return {
    company: state.user.company,
    language: state.languages,
  };
}

const WrappedAddInvoice = Form.create()(AddInvoice);

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(WrappedAddInvoice);

/**
 * Created by zhouli on 18/3/9
 * Email li.zhou@huilianyi.com
 * 法人实体的创建与更新
 * 选择账套列表
 * 选择上级法人
 * 上传二维码图片
 * 提交表单创建法人
 *
 * 有点注意：多语言的校验单独处理，没有在表单校验
 */
import React from 'react';
import { Button, Form, Input, Col, Row, Switch, message, Icon, Select } from 'antd';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';

import config from 'config';
import 'styles/enterprise-manage/legal-person/new-legal-person.scss';
import LPService from 'containers/enterprise-manage/legal-person/legal-person.service';
import Selector from 'components/Widget/selector';
import { LanguageInput } from 'components/Widget/index';
import { isEmptyObj, getLanguageName } from 'utils/extend';
import { messages } from 'utils/utils';
const FormItem = Form.Item;
const Option = Select.Option;
import { ImageUpload } from 'components/Widget/index';

class NewLegalPerson extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      //上级法人下拉单
      parentLegalEntity: {
        url:
          `${config.mdataUrl}/api/find/parent/legalentitys?legalEntityId=` +
          (this.props.match.params.legalPersonID === ':legalPersonID'
            ? ''
            : this.props.match.params.legalPersonID),
        label: record => `${record.entityName}`,
        key: 'id',
      },
      //选择账套下拉单
      sob: {
        url: `${config.mdataUrl}/api/setOfBooks/by/tenant`,
        label: record => `${record.setOfBooksName}`,
        key: 'id',
      },
      uploadedImages: [], //已经上传的图片
      //账套
      //默认的法人实体
      legalPerson: {
        accountBank: '', //开户行
        address: '', //地址
        entityName: '', //名称

        accountNumber: '', //银行卡号
        enabled: true, //状态
        i18n: {}, //包含开户行，地址，名称

        setOfBooksId: null, //账套
        telePhone: '', //电话
        taxpayerNumber: '', //税号

        parentLegalEntityId: '', //上级法人
        attachmentId: '', //发票二维码上传图片后的id
        mainLanguage: 'zh_cn', //开票显示语言
        mainLanguageName: this.$t('legal.person.new.chinese'), //简体中文
        i118entityName: null, //对应的是companyName多语言
        i118accountBank: null,
        i118address: null,
      },
    };
  }

  componentWillMount() {
    // 请求
    // 账套列表
    // 法人列表
  }

  componentDidMount() {
    if (this.props.match.params.legalPersonOid === ':legalPersonOid') {
    } else {
      this.getLegalPersonDetail();
    }
  }

  getLegalPersonDetail = () => {
    LPService.getLegalPersonDetail(this.props.match.params.legalPersonOid).then(res => {
      let data = res.data;
      let uploadedImages = [];
      let uploadedImage = this.getUploadedImage(data);
      if (!isEmptyObj(uploadedImage)) {
        uploadedImages = [uploadedImage];
      }
      //前端设置一下语言
      data.mainLanguageName = getLanguageName(data.mainLanguage, this.props.language.languageType);
      this.setState(
        {
          legalPerson: data,
          uploadedImages,
        },
        () => {}
      );
    });
  };

  getUploadedImage = data => {
    let uploadedImage = {};
    if (data.iconURL) {
      uploadedImage.iconURL = data.iconURL;
    }
    if (data.fileURL) {
      uploadedImage.fileURL = data.fileURL;
    }
    if (data.thumbnailUrl) {
      uploadedImage.thumbnailUrl = data.thumbnailUrl;
    }
    if (data.attachmentId) {
      uploadedImage.attachmentOid = data.attachmentId;
      uploadedImage.fileName = data.fileName;
    }
    return uploadedImage;
  };

  //校验多语言
  validateI18n = legalPerson => {
    if (
      legalPerson.entityName === '' ||
      legalPerson.entityName === undefined ||
      legalPerson.entityName === null
    ) {
      // 请填写法人实体名称
      message.error(this.$t('legal.person.new.title.p'));
      return false;
    }
    if (
      legalPerson.accountBank === '' ||
      legalPerson.accountBank === undefined ||
      legalPerson.accountBank === null
    ) {
      // 请填写开户行
      message.error(this.$t('legal.person.new.account.p'));
      return false;
    }
    if (
      legalPerson.address === '' ||
      legalPerson.address === undefined ||
      legalPerson.address === null
    ) {
      // 请填写地址
      message.error(this.$t('legal.person.new.address.p'));
      return false;
    }
    return true;
  };

  handleSave = e => {
    e.preventDefault();
    let _legalPerson = this.state.legalPerson;
    if (this.validateI18n(_legalPerson)) {
      this.props.form.validateFieldsAndScroll((err, values) => {
        if (!err) {
          if (this.props.match.params.legalPersonOid === ':legalPersonOid') {
            //创建法人实体
            let legalPerson = Object.assign({}, values, {
              attachmentId: values['attachmentId'][0] ? values['attachmentId'][0].id : '',
              //attachmentId: this.state.uploadedImages[0].uid,
              i18n: _legalPerson.i18n,
              entityName: _legalPerson.entityName,
              accountBank: _legalPerson.accountBank,
              address: _legalPerson.address,
              mainLanguage: _legalPerson.mainLanguage,
            });
            this.createLegalPerson(legalPerson);
          } else {
            //更新法人实体
            if (values.setOfBooksId === _legalPerson.setOfBooksName) {
              values.setOfBooksId = _legalPerson.setOfBooksId;
            }
            if (values.parentLegalEntityId === _legalPerson.parentLegalEntityName) {
              values.parentLegalEntityId = _legalPerson.parentLegalEntityId;
            }
            //判断等于'string'是为什么？增加 或判断等于undefined
            if (
              typeof values['attachmentId'] === 'string' ||
              typeof values['attachmentId'] === 'undefined'
            ) {
              //这种情况是不用改的
            } else {
              if (values['attachmentId'] && values['attachmentId'][0]) {
                values['attachmentId'] = values['attachmentId'][0].id;
              } else {
                values['attachmentId'] =
                  this.state.uploadedImages.length > 0 ? this.state.uploadedImages[0].uid : null;
              }
            }
            let legalPerson = Object.assign(_legalPerson, values, {
              i18n: _legalPerson.i18n,
              entityName: _legalPerson.entityName,
              accountBank: _legalPerson.accountBank,
              address: _legalPerson.address,
              mainLanguage: _legalPerson.mainLanguage,
            });
            !legalPerson.parentLegalEntityId && (legalPerson.parentLegalEntityId = '');
            this.handleUpdate(legalPerson);
          }
        }
      });
    }
  };
  //保存所做的详情修改
  handleUpdate = legalPerson => {
    LPService.updateLegalPerson(legalPerson)
      .then(res => {
        this.props.dispatch(
          routerRedux.replace({
            pathname: '/enterprise-manage/legal-person/legal-person',
          })
        );
        this.setState({
          legalPerson: res.data,
        });
      })
      .catch(res => {});
  };
  //创建法人实体
  createLegalPerson = legalPerson => {
    this.setState({
      loading: true,
    });

    LPService.createLegalPerson(legalPerson)
      .then(res => {
        this.setState({
          loading: false,
        });
        this.props.dispatch(
          routerRedux.replace({
            pathname: '/enterprise-manage/legal-person/legal-person',
          })
        );
      })
      .catch(res => {
        this.setState({
          loading: false,
        });
      });
  };
  //点击取消，返回
  handleCancel = e => {
    e.preventDefault();
    this.props.dispatch(
      routerRedux.replace({
        pathname: '/enterprise-manage/legal-person/legal-person',
      })
    );
  };

  handleChange = e => {
    if (this.state.loading) {
      this.setState({
        loading: false,
      });
    }
  };

  //上传图片后的回调函数
  handleUploadImageChange = fileList => {
    let { uploadedImages } = this.state;
    this.setState({
      uploadedImages: fileList,
    });
  };

  //地址：多语言
  i18nAddressChange = (name, i18nName) => {
    const legalPerson = this.state.legalPerson;
    legalPerson.address = name;
    legalPerson.i18n.address = i18nName;
  };

  //开户行：多语言
  i18nAccountBankChange = (name, i18nName) => {
    const legalPerson = this.state.legalPerson;
    legalPerson.accountBank = name;
    legalPerson.i18n.accountBank = i18nName;
  };
  //法人实体名称：多语言
  i18nCompanyNameChange = (name, i18nName) => {
    const legalPerson = this.state.legalPerson;
    legalPerson.entityName = name;
    legalPerson.i18n.entityName = i18nName;
  };
  //渲染语言
  renderLanguageList = list => {
    if (list.length > 1) {
      return list.map(item => {
        return (
          <Option value={item.code} key={item.code}>
            {item.comments}
          </Option>
        );
      });
    } else {
      return (
        <Option value="zh_cn" key={1}>
          {/*简体中文*/}
          {messages('legal.person.new.chinese')}
        </Option>
      );
    }
  };
  handleLanguage = value => {
    const { legalPerson } = this.state;
    legalPerson.mainLanguage = value;
    legalPerson.mainLanguageName = getLanguageName(value, this.props.languageList);
    //语言
    this.setState({
      legalPerson,
    });
  };

  render() {
    const { getFieldDecorator } = this.props.form;
    const { legalPerson, loading, uploadedImages } = this.state;
    return (
      <div className="new-legal-person-wrap" style={{ marginBottom: 20 }}>
        <Form onSubmit={this.handleSave} onChange={this.handleChange}>
          <Row gutter={24} className="new-lp-row-wrap">
            <Col span={8}>
              <div className="new-lp-row">
                <span className="new-lp-row-re">*</span>
                <span>
                  {/*法人实体名称*/}
                  {this.$t('legal.person.new.name')}
                </span>
              </div>
              <LanguageInput
                key={1}
                name={legalPerson.entityName}
                i18nName={legalPerson.i18n.entityName ? legalPerson.i18n.entityName : null}
                isEdit={legalPerson.id}
                nameChange={this.i18nCompanyNameChange}
              />
            </Col>
            <Col span={8}>
              <div className="new-lp-row">
                <span className="new-lp-row-re">*</span>
                <span>
                  {/*开户行*/}
                  {this.$t('legal.person.new.account')}
                </span>
              </div>
              <LanguageInput
                key={2}
                name={legalPerson.accountBank}
                i18nName={legalPerson.i18n.accountBank ? legalPerson.i18n.accountBank : null}
                isEdit={legalPerson.id}
                nameChange={this.i18nAccountBankChange}
              />
            </Col>
            <Col span={8}>
              <div className="new-lp-row">
                <span className="new-lp-row-re">*</span>
                <span>
                  {/*地址*/}
                  {this.$t('legal.person.new.address')}
                </span>
              </div>
              <LanguageInput
                key={3}
                name={legalPerson.address}
                i18nName={legalPerson.i18n.address ? legalPerson.i18n.address : null}
                isEdit={legalPerson.id}
                nameChange={this.i18nAddressChange}
              />
            </Col>
          </Row>
          <Row gutter={24}>
            <Col span={8}>
              <FormItem label={this.$t('legal.person.new.mobile')} /* 电话*/ colon={true}>
                {getFieldDecorator('telePhone', {
                  initialValue: legalPerson.telePhone,
                  rules: [
                    {
                      required: true,
                      message: this.$t('common.please.enter'),
                    },
                    {
                      max: 20,
                      message: this.$t('legal.person.new.tips3'), //"不能超过20个字符"
                    },
                    {
                      message: this.$t('legal.person.new.tips2'), //"只能是数字与-",
                      validator: (rule, value, cb) => {
                        if (value === null || value === undefined || value === '') {
                          cb();
                          return;
                        }
                        var regExp = /^[0-9\- ]+$/i;
                        //去掉空格
                        value = value.replace(/ /g, '');
                        if (value.length <= 20 && regExp.test(value)) {
                          cb();
                        } else {
                          cb(false);
                        }
                      },
                    },
                  ],
                })(<Input placeholder={this.$t('common.please.enter')} />)}
              </FormItem>
            </Col>
            <Col span={8}>
              <FormItem label={this.$t('legal.person.new.tax')} /* 纳税人识别号*/ colon={true}>
                {getFieldDecorator('taxpayerNumber', {
                  initialValue: legalPerson.taxpayerNumber,
                  rules: [
                    {
                      required: true,
                      message: this.$t('common.please.enter'),
                    },
                    {
                      message: this.$t('legal.person.new.tips1'), //"纳税人识别号数字与字母，长度不能超过30",
                      validator: (rule, value, cb) => {
                        if (value === null || value === undefined || value === '') {
                          cb();
                          return;
                        }
                        let regExp = /^[a-z0-9_ ]+$/i;
                        //去掉空格
                        value = value.replace(/ /g, '');
                        if (value.length <= 30 && regExp.test(value)) {
                          cb();
                        } else {
                          cb(false);
                        }
                      },
                    },
                  ],
                })(<Input placeholder={this.$t('common.please.enter')} />)}
              </FormItem>
            </Col>
            <Col span={8}>
              {/*todo*/}
              {/*必须是数字或者减号*/}
              <FormItem label={this.$t('legal.person.new.bank.card')} /* 银行账号*/ colon={true}>
                {getFieldDecorator('accountNumber', {
                  initialValue: legalPerson.accountNumber,
                  rules: [
                    { required: true, message: this.$t('common.please.enter') },
                    {
                      message: this.$t('legal.person.new.tips2'), //"只能是数字与-",
                      validator: (rule, value, cb) => {
                        if (value === null || value === undefined || value === '') {
                          cb();
                          return;
                        }
                        let regExp = /^[0-9\- ]+$/i;
                        //去掉空格
                        value = value.replace(/ /g, '');
                        if (value.length <= 30 && regExp.test(value)) {
                          cb();
                        } else {
                          cb(false);
                        }
                      },
                    },
                    {
                      max: 30,
                      message: this.$t('legal.person.new.tips4'), //不能超过30个字符
                    },
                  ],
                })(<Input placeholder={this.$t('common.please.enter')} />)}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>
            <Col span={8}>
              <FormItem label={this.$t('legal.person.new.sob')} /* 账套*/ colon={true}>
                {getFieldDecorator('setOfBooksId', {
                  initialValue: legalPerson.setOfBooksName,
                  rules: [
                    {
                      required: true,
                      message: this.$t('common.please.select'),
                    },
                  ],
                })(
                  <Selector
                    placeholder={this.$t('common.please.select')}
                    disabled={!!legalPerson.setOfBooksId}
                    selectorItem={this.state.sob}
                  />
                )}
              </FormItem>
            </Col>
            <Col span={8}>
              <FormItem label={this.$t('legal.person.new.parent.legal')} /* 上级法人*/ colon={true}>
                {getFieldDecorator('parentLegalEntityId', {
                  initialValue: legalPerson.parentLegalEntityName,
                })(
                  <Selector
                    placeholder={this.$t('common.please.select')}
                    selectorItem={this.state.parentLegalEntity}
                  />
                )}
              </FormItem>
            </Col>
            <Col span={8}>
              {/*状态*/}
              <FormItem label={this.$t('common.status', { status: '' })} colon={true}>
                {getFieldDecorator('enabled', {
                  initialValue: legalPerson.enabled,
                  valuePropName: 'checked',
                })(
                  <Switch
                    checkedChildren={<Icon type="check" />}
                    unCheckedChildren={<Icon type="cross" />}
                  />
                )}
              </FormItem>
            </Col>
          </Row>

          <Row gutter={24}>
            <Col span={8}>
              <FormItem
                label={this.$t('legal.person.new.mainLanguage')} /* 开票显示语言*/
                colon={true}
              >
                {getFieldDecorator('mainLanguage', {
                  initialValue: legalPerson.mainLanguageName,
                })(
                  <Select
                    className="select-language"
                    showSearch
                    placeholder={this.$t('common.please.select')}
                    optionFilterProp="children"
                    onChange={this.handleLanguage}
                    filterOption={(input, option) =>
                      option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
                    }
                  >
                    {this.renderLanguageList(this.props.language.languageType)}
                  </Select>
                )}
              </FormItem>
            </Col>
          </Row>

          <Row>
            <Col>
              <FormItem label={this.$t('legal.person.new.qcode')} /* 二维码*/ colon={true}>
                {getFieldDecorator('attachmentId', {
                  initialValue: legalPerson.attachmentId,
                  rules: [],
                })(
                  <ImageUpload
                    attachmentType="INVOICE_IMAGES"
                    uploadUrl={`${config.baseUrl}/api/upload/static/attachment`}
                    fileType={['PNG', 'png', 'jpeg', 'jpeg', 'jpg', 'JPG', 'bmp', 'BMP']}
                    defaultFileList={uploadedImages}
                    onChange={this.handleUploadImageChange}
                    isShowDefault={true}
                    maxNum={1}
                  />
                )}
              </FormItem>
            </Col>
          </Row>
          <div>
            <Button type="primary" loading={loading} htmlType="submit">
              {this.$t('common.save') /*保存*/}
            </Button>
            <Button onClick={this.handleCancel} style={{ marginLeft: 8 }}>
              {this.$t('common.cancel') /*取消*/}
            </Button>
          </div>
        </Form>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    languageList: state.languages.languageList,
    language: state.languages,
    user: state.user.currentUser,
    company: state.user.company,
  };
}

const WrappedNewLegalPerson = Form.create()(NewLegalPerson);

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(WrappedNewLegalPerson);
import React, {useEffect, useState} from 'react';
import './Account.css'
import add from '../../images/add.png'
import {
    AuthInfo,
    cryptoDeleteData,
    cryptoGetAllType,
    cryptoGetGeneratePrivateKey,
    cryptoPostData, sendTransaction
} from "../../requests";
import Cookie from "js-cookie";
import crypto from '../../images/bitcoin.png'
import bucket from '../../images/bucket.png'
import down from '../../images/down.png'
import {useDispatch} from "react-redux";

const Account = () => {
    const dispatch = useDispatch();
    const [myAddresses, setMyAddresses] = useState([]);
    const [activeAddresses, setActiveAddresses] = useState([]);
    const [listAddresses, setListAddresses] = useState([]);
    const [page, setPage] = useState(1);
    const [isAdd, setIsAdd] = useState(false)
    const [menuChoice, setMenuChoice] = useState(1);
    const [myTransactionAddresses, setMyTransactionAddresses] = useState([]);
    const [recipientTransactionAddresses, setRecipientTransactionAddresses] = useState([]);
    const [userInfo, setUserInfo] = useState({});
    const [inputAddressValue, setInputAddressValue] = useState('');

    const jwt = Cookie.get('jwt');



    useEffect(() => {
        setListAddresses(myAddresses)
    }, [myAddresses]);

    useEffect(() => {
        if (jwt) {
            AuthInfo(jwt).then(response => {
                    if (response.status === 401) {
                        throw new Error();
                    } else {
                        setUserInfo(response.data);
                    }
                }
            ).catch(e => {
                dispatch({type: "SET_IS_AUTH", isAuth: false});
                Cookie.remove('jwt');
            })
        }
        cryptoGetAllType(jwt).then(response => {
            setMyAddresses(response.data)
        })
    }, []);

    useEffect(() => {
        let arr = []
        for (let i = (page - 1) * 3; i < page * 3 && i < myAddresses.length; i++) {
            arr.push(myAddresses[i]);
        }
        setActiveAddresses(arr);
    }, [myAddresses, page]);


    const handlePageClick = (e) => {
        if(+e.target.innerHTML !== 0 && (+e.target.innerHTML-1)*3 < myAddresses.length) {
            setPage(+e.target.innerHTML)
        }
    }

    const handleAddClick = (e) => {
        setIsAdd(!isAdd);
        if (!isAdd) {
            e.target.style.transform = 'rotate(45deg)'
            document.querySelector('.account-content').style.display = 'block';
        } else {
            e.target.style.transform = 'rotate(0deg)'
            document.querySelector('.account-content').style.display = 'flex';
        }
    }

    const myAddButtonClick = () => {
        setMyTransactionAddresses([...myTransactionAddresses, {}]);
    }

    const recipientAddButtonClick = () => {
        setRecipientTransactionAddresses([...recipientTransactionAddresses, []])
    }

    const myTransactionAddressOnClick = (e) => {
        const list = e.currentTarget.nextElementSibling;
        list.classList.toggle('address-list-active')
    }


    const myAddressListItemOnClick = (e) => {



        const elem = e.currentTarget.parentElement.previousElementSibling.firstElementChild;
        e.currentTarget.parentElement.classList.toggle('address-list-active');
        elem.innerHTML = e.target.innerHTML;
        const lists = document.querySelectorAll('.my-address-list');
        const parent = e.currentTarget.parentElement;
        let index;
        for (let i = 0; i < lists.length; i++) {
            if (parent === lists[i]) {
                index = i;
                break;
            }
        }

        let deleteIndex;
        for (let i = 0; i < listAddresses.length; i++) {
            if (listAddresses[i] === e.target.innerHTML) {
                deleteIndex = i;
                break;
            }
        }

        let temp = [...listAddresses];
        temp.splice(deleteIndex, 1)
        setListAddresses(temp);

        temp = myTransactionAddresses;
        const address = myAddresses.find(({address}) => address === e.target.innerHTML);
        temp[index] = [address];
        setMyTransactionAddresses(temp);
    }

    const submitAddAddress = () => {
        cryptoPostData(jwt, {type: "BTC", private_key: inputAddressValue}).then(response => {
            setMyAddresses([...myAddresses, response.data])
        })
    }

    const deleteCrypto = (e) => {
        const address = myAddresses.find(({address}) => address === e.currentTarget.previousElementSibling.innerHTML);
        const deleteId = address.id;
        cryptoDeleteData(jwt, deleteId);

        let arr = myAddresses;
        arr = arr.filter(({id}) => id !== deleteId);
        setMyAddresses(arr);
    }

    const generateAddress = (e) => {
        cryptoGetGeneratePrivateKey(jwt).then(response => {
            setInputAddressValue(response.data.private_key)
        })
    }

    const submitTransaction = () => {

        let outsArray = []
        const outs = document.querySelectorAll('.recipient-transaction-address-input')

        for (let i = 0; i < outs.length; i++) {
            outsArray.push({key: i, address: outs[i].firstElementChild.value, amount: +outs[i].lastElementChild.value})
        }

        const keysArray = []
        for (let i = 0; i < outs.length; i++) {
            keysArray.push(i);
        }

        let inputsArray = []

        for (let i = 0; i < myTransactionAddresses.length; i++) {
            if (myTransactionAddresses[i][0]) {
                inputsArray.push({keys: keysArray, crypto_data_id: myTransactionAddresses[i][0].id})
            }
        }

        const data = {
            inputs: inputsArray,
            outs: outsArray

        }


        sendTransaction(jwt, data)

    }

    return (
        <div className="account">
            <div className="account-menu">
                <div className="account-menu-item" onClick={() => setMenuChoice(1)}>Мои адреса</div>
                <div className="account-menu-item" onClick={() => setMenuChoice(2)}>Создать транзакцию</div>
            </div>
            <div className="account-block">
                <div className="account-content">
                    {menuChoice === 1 ?
                        <>
                            <div className="account-item">
                                <div className="account-btn">
                                    <img onClick={handleAddClick} src={add} alt=""/>
                                </div>
                            </div>
                            {isAdd ?
                                <div className="add-address-form">
                                    <div className="add-input"><input
                                        onChange={(e) => setInputAddressValue(e.target.value)} value={inputAddressValue}
                                        type="text"/></div>
                                    <div className="add-btn" onClick={generateAddress}>Сгенерировать адрес</div>
                                    <div className="add-btn" onClick={submitAddAddress}>Добавить адрес</div>
                                </div>
                                :
                                <>
                                    <div className="addresses">
                                        {activeAddresses.map(address =>
                                            <div className="address" key={address.id}>
                                                <div className="crypto-image"><img src={crypto} alt=""/></div>
                                                <div className="address-value">{address.address}</div>
                                                <div className="address-bucket" onClick={deleteCrypto}><img src={bucket}
                                                                                                            alt=""/>
                                                </div>
                                            </div>
                                        )}
                                    </div>
                                    <div className="pages">
                                        <div onClick={handlePageClick} className="page">{page - 1}</div>
                                        <div onClick={handlePageClick} className="page active-page">{page}</div>
                                        <div onClick={handlePageClick} className="page">{page + 1}</div>
                                    </div>
                                </>

                            }
                        </>
                        :
                        <div className="transaction">
                            <div className="transaction-content">
                                <div className="transaction-my">
                                    <div className="transaction-header">
                                        <div className="my-add-btn" onClick={myAddButtonClick}>
                                            <img src={add} alt=""/>
                                        </div>
                                        <div className="transaction-title">Мои адреса</div>
                                    </div>
                                    <div className="transaction-main">
                                        {myTransactionAddresses.map((address, index) =>
                                            <div key={index}>
                                                <div className="my-transaction-address"
                                                     onClick={myTransactionAddressOnClick}>
                                                    <div className="my-transaction-address-value">Выберите адрес</div>
                                                    <div className="my-transaction-address-img">
                                                        <img src={down} alt=""/>
                                                    </div>
                                                </div>
                                                <div className="my-address-list">
                                                    {listAddresses.map(address =>
                                                        <div className="my-address-list-item" key={address.id}
                                                             onClick={myAddressListItemOnClick}>
                                                            {address.address}
                                                        </div>
                                                    )}
                                                </div>
                                            </div>
                                        )}
                                    </div>
                                </div>
                                <div className="transaction-recipient">
                                    <div className="transaction-header">
                                        <div className="transaction-title">Адреса получаетелей</div>
                                        <div className="recipient-add-btn" onClick={recipientAddButtonClick}>
                                            <img src={add} alt=""/>
                                        </div>
                                    </div>
                                    <div className="transaction-main">
                                        {recipientTransactionAddresses.map((address, index) =>
                                            <div key={index} className="recipient-transaction-address">
                                                <div className="recipient-transaction-address-input">
                                                    <input type="text" placeholder="Адрес"/>
                                                    <input type="number" placeholder="Кол-во"/>
                                                </div>
                                            </div>
                                        )}
                                    </div>

                                </div>
                            </div>
                            <div className="transaction-submit-btn" onClick={submitTransaction}>
                                Создать транзакцию
                            </div>
                        </div>
                    }
                </div>
            </div>
        </div>
    );
};

export default Account;
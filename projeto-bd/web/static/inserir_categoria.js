function foo() {
    document.getElementById('escolher_categoria').remove();
    document.getElementById('bottom').insertAdjacentHTML("beforebegin", `
        <p>Escreva o nome da categoria filha:</p>
            <input type="text" name="nome_da_categoria">
        <div id="escolher_categoria">
            <p>Escolha o tipo da categoria:</p>
                <input id="categoria_simples_btn" type="radio" name="tipo_da_categoria" value="categoria_simples" checked="checked">
                    <label>Categoria Simples</label><br>
                <input onClick="foo()" id="super_categoria_btn" type="radio" name="tipo_da_categoria" value="super_categoria">
                    <label>Super Categoria</label><br>
            </div>
        <br>
    `);
}
